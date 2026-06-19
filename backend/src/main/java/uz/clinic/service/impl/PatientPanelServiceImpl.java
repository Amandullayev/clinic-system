package uz.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.clinic.dto.request.AppointmentRequest;
import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.entity.*;
import uz.clinic.enums.AppointmentStatus;
import uz.clinic.enums.errors.ErrorType;
import uz.clinic.exception.AppException;
import uz.clinic.mapper.AppointmentMapper;
import uz.clinic.repository.*;
import uz.clinic.service.AppointmentValidator;
import uz.clinic.service.EmailService;
import uz.clinic.service.PatientPanelService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientPanelServiceImpl implements PatientPanelService {

    private final AppointmentRepository    appointmentRepository;
    private final PatientRepository        patientRepository;
    private final DoctorRepository         doctorRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final UserRepository           userRepository;
    private final AppointmentMapper        appointmentMapper;
    private final AppointmentValidator     appointmentValidator;
    private final EmailService             emailService;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private Patient getPatientByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorType.USER_NOT_FOUND));
        return patientRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorType.PATIENT_NOT_FOUND));
    }

    @Override
    public List<AppointmentResponse> getMyAppointments(String email) {
        Patient patient = getPatientByEmail(email);
        return appointmentRepository.findAllByPatientId(patient.getId())
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request, String email) {
        Patient patient = getPatientByEmail(email);

        // YANGI: online booking bloklanganmi tekshirish
        if (patient.isOnlineBookingBlocked())
            throw new AppException(ErrorType.APPOINTMENT_BOOKING_BLOCKED);

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new AppException(ErrorType.DOCTOR_NOT_FOUND));

        MedicalService service = medicalServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorType.SERVICE_NOT_FOUND));

        // Ish vaqti, band slot va boshqa tekshiruvlar
        appointmentValidator.validate(doctor, request.getAppointmentTime(), null);

        // YANGI: shu kun uchun navbat raqami hisoblash
        LocalDateTime dayStart = request.getAppointmentTime().toLocalDate().atStartOfDay();
        LocalDateTime dayEnd   = dayStart.plusDays(1);
        long todayCount = appointmentRepository
                .countActiveForDoctorOnDay(doctor.getId(), dayStart, dayEnd);
        int queueNumber = (int) todayCount + 1;

        // YANGI: tasdiqlash token — UUID, bir martalik
        String confirmToken = UUID.randomUUID().toString();

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .medicalService(service)
                .appointmentTime(request.getAppointmentTime())
                .notes(request.getNotes())
                .status(AppointmentStatus.PENDING)
                .confirmToken(confirmToken)
                .queueNumber(queueNumber)
                .build();

        appointmentRepository.save(appointment);

        // YANGI: tasdiqlash emaili yuborish
        String patientEmail = patient.getEmail() != null
                ? patient.getEmail()
                : patient.getUser().getEmail();

        try {
            emailService.sendAppointmentConfirmationRequest(
                    patientEmail,
                    patient.getFullName(),
                    doctor.getUser().getFullName(),
                    appointment.getAppointmentTime().format(FORMATTER),
                    confirmToken);
        } catch (Exception e) {
            // Email yuborilmasa ham appointment saqlanadi —
            // scheduler 24 soatdan keyin avtomatik bekor qiladi
            log.error("Tasdiqlash emaili yuborishda xato: appointmentId={}",
                    appointment.getId(), e);
        }

        return appointmentMapper.toResponse(appointment);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId, String email) {
        Patient patient = getPatientByEmail(email);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException(ErrorType.APPOINTMENT_NOT_FOUND));

        if (!appointment.getPatient().getId().equals(patient.getId()))
            throw new AppException(ErrorType.APPOINTMENT_CANCEL_FORBIDDEN);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED)
            throw new AppException(ErrorType.APPOINTMENT_ALREADY_COMPLETED);

        // YANGI: qabuldan 2 soat oldindan kam qolganida bekor qilish mumkin emas
        long minutesLeft = java.time.temporal.ChronoUnit.MINUTES.between(
                LocalDateTime.now(), appointment.getAppointmentTime());
        if (minutesLeft < 120)
            throw new AppException(ErrorType.APPOINTMENT_CANCEL_TOO_LATE);

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setConfirmToken(null);
        appointmentRepository.save(appointment);
    }

    // YANGI: email havolasidagi token orqali tasdiqlash
    @Override
    @Transactional
    public AppointmentResponse confirmByToken(String token) {
        Appointment appointment = appointmentRepository.findByConfirmToken(token)
                .orElseThrow(() -> new AppException(ErrorType.APPOINTMENT_NOT_FOUND));

        if (appointment.getStatus() != AppointmentStatus.PENDING)
            throw new AppException(ErrorType.APPOINTMENT_ALREADY_CONFIRMED);

        // Vaqti o'tib ketgan bo'lsa tasdiqlab bo'lmaydi
        if (appointment.getAppointmentTime().isBefore(LocalDateTime.now()))
            throw new AppException(ErrorType.APPOINTMENT_IN_PAST);

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setConfirmedAt(LocalDateTime.now());
        appointment.setConfirmToken(null); // token bir martalik — o'chiriladi
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    // YANGI: email havolasidagi token orqali bekor qilish
    @Override
    @Transactional
    public void cancelByToken(String token) {
        Appointment appointment = appointmentRepository.findByConfirmToken(token)
                .orElseThrow(() -> new AppException(ErrorType.APPOINTMENT_NOT_FOUND));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED)
            throw new AppException(ErrorType.APPOINTMENT_ALREADY_COMPLETED);

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setConfirmToken(null);
        appointmentRepository.save(appointment);
    }

    @Override
    public List<String> getAvailableSlots(Long doctorId, String dateStr) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new AppException(ErrorType.DOCTOR_NOT_FOUND));

        LocalDate date = LocalDate.parse(dateStr);
        DoctorSchedule schedule = doctor.getScheduleFor(date.getDayOfWeek());
        if (schedule == null) return Collections.emptyList();

        LocalTime startTime = schedule.getStartTime();
        LocalTime endTime   = schedule.getEndTime();
        LocalTime lastSlot  = endTime.minusMinutes(30);

        List<String> allSlots = new ArrayList<>();
        LocalTime cur = startTime;
        while (!cur.isAfter(lastSlot)) {
            allSlots.add(cur.toString());
            cur = cur.plusMinutes(30);
        }

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd   = date.atTime(23, 59, 59);

        // YANGI: PENDING va CONFIRMED ham band hisoblanadi (AUTO_CANCELLED va CANCELLED emas)
        List<Appointment> booked = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetweenAndStatusNot(
                        doctorId, dayStart, dayEnd, AppointmentStatus.CANCELLED);

        Set<String> bookedTimes = booked.stream()
                .filter(a -> a.getStatus() != AppointmentStatus.AUTO_CANCELLED
                        && a.getStatus() != AppointmentStatus.NO_SHOW)
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toSet());

        LocalDateTime now = LocalDateTime.now();
        return allSlots.stream()
                .filter(slot -> !bookedTimes.contains(slot))
                .filter(slot -> LocalDateTime.of(date, LocalTime.parse(slot)).isAfter(now))
                .collect(Collectors.toList());
    }
}