package uz.clinic.service.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.clinic.entity.Appointment;
import uz.clinic.entity.Patient;
import uz.clinic.enums.AppointmentStatus;
import uz.clinic.repository.AppointmentRepository;
import uz.clinic.repository.PatientRepository;
import uz.clinic.service.EmailService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// YANGI: avtomatik status o'zgartirish va email yuborish.
// @EnableScheduling — ClinicApplication.java ga qo'shish kerak.
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentScheduler {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository     patientRepository;
    private final EmailService emailService;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    // Har 5 daqiqada: vaqti 15 daqiqa o'tgan CONFIRMED → NO_SHOW
    @Scheduled(fixedRate = 300_000)
    @Transactional
    public void autoMarkNoShow() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);
        List<Appointment> expired = appointmentRepository.findExpiredConfirmed(threshold);

        for (Appointment a : expired) {
            a.setStatus(AppointmentStatus.NO_SHOW);
            appointmentRepository.save(a);

            // Bemor no-show hisobini oshirish
            Patient patient = a.getPatient();
            int newCount = (patient.getNoShowCount() == null ? 0 : patient.getNoShowCount()) + 1;
            patient.setNoShowCount(newCount);

            // 3 martadan keyin online booking bloklanadi
            if (newCount >= 3) {
                patient.setOnlineBookingBlocked(true);
                log.info("Bemor online booking bloklandи: patientId={}", patient.getId());
            }
            patientRepository.save(patient);

            // Email yuborish
            try {
                String email = patient.getEmail() != null
                        ? patient.getEmail()
                        : (patient.getUser() != null ? patient.getUser().getEmail() : null);
                if (email != null) {
                    emailService.sendNoShowWarning(email, patient.getFullName(), newCount);
                }
            } catch (Exception e) {
                log.error("No-show email yuborishda xato: appointmentId={}", a.getId(), e);
            }

            log.info("NO_SHOW belgilandi: appointmentId={}, patientId={}, noShowCount={}",
                    a.getId(), patient.getId(), newCount);
        }
    }

    // Har 1 soatda: 24 soat ichida tasdiqlanmagan PENDING → AUTO_CANCELLED
    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void autoCancelUnconfirmed() {
        LocalDateTime deadline = LocalDateTime.now().minusHours(24);
        List<Appointment> unconfirmed = appointmentRepository.findUnconfirmedBefore(deadline);

        for (Appointment a : unconfirmed) {
            a.setStatus(AppointmentStatus.AUTO_CANCELLED);
            appointmentRepository.save(a);

            log.info("AUTO_CANCELLED: appointmentId={}, patientId={}",
                    a.getId(), a.getPatient().getId());
        }

        if (!unconfirmed.isEmpty()) {
            log.info("Jami {} ta navbat avtomatik bekor qilindi", unconfirmed.size());
        }
    }

    // Har 30 daqiqada: 2 soatdan keyin bo'ladigan CONFIRMED appointmentlarga eslatma
    @Scheduled(fixedRate = 1_800_000)
    @Transactional
    public void sendReminders() {
        LocalDateTime from = LocalDateTime.now().plusHours(2);
        LocalDateTime to   = LocalDateTime.now().plusHours(2).plusMinutes(30);

        List<Appointment> upcoming = appointmentRepository.findConfirmedBetween(from, to);

        for (Appointment a : upcoming) {
            try {
                Patient patient = a.getPatient();
                String email = patient.getEmail() != null
                        ? patient.getEmail()
                        : (patient.getUser() != null ? patient.getUser().getEmail() : null);

                if (email != null) {
                    emailService.sendAppointmentReminder(
                            email,
                            patient.getFullName(),
                            a.getDoctor().getUser().getFullName(),
                            a.getAppointmentTime().format(FORMATTER));
                    log.info("Eslatma yuborildi: appointmentId={}", a.getId());
                }
            } catch (Exception e) {
                log.error("Eslatma emailida xato: appointmentId={}", a.getId(), e);
            }
        }
    }
}