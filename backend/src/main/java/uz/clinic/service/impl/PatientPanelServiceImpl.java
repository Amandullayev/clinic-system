package uz.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.dto.request.AppointmentRequest;
import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.entity.Appointment;
import uz.clinic.entity.Doctor;
import uz.clinic.entity.MedicalService;
import uz.clinic.entity.Patient;
import uz.clinic.entity.User;
import uz.clinic.enums.AppointmentStatus;
import uz.clinic.exception.BadRequestException;
import uz.clinic.exception.ResourceNotFoundException;
import uz.clinic.mapper.AppointmentMapper;
import uz.clinic.repository.AppointmentRepository;
import uz.clinic.repository.DoctorRepository;
import uz.clinic.repository.MedicalServiceRepository;
import uz.clinic.repository.PatientRepository;
import uz.clinic.repository.UserRepository;
import uz.clinic.service.PatientPanelService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PatientPanelServiceImpl implements PatientPanelService {

    private final AppointmentRepository   appointmentRepository;
    private final PatientRepository       patientRepository;
    private final DoctorRepository        doctorRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final UserRepository          userRepository;
    private final AppointmentMapper       appointmentMapper;

    private Patient getPatientByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));
        return patientRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Bemor topilmadi"));
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
    public AppointmentResponse bookAppointment(AppointmentRequest request, String email) {
        Patient patient = getPatientByEmail(email);

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Shifokor topilmadi"));

        MedicalService service = medicalServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Xizmat topilmadi"));

        if (request.getAppointmentTime() == null)
            throw new BadRequestException("Qabul vaqti kiritilishi shart");

        LocalDateTime apptTime = request.getAppointmentTime();

        // 1. Ish kunini tekshirish
        if (doctor.getWorkingDays() != null && !doctor.getWorkingDays().isEmpty()) {
            int dayValue = apptTime.getDayOfWeek().getValue();
            boolean validDay = doctor.getWorkingDays().stream()
                    .map(String::trim)
                    .anyMatch(d -> {
                        try { return Integer.parseInt(d) == dayValue; }
                        catch (NumberFormatException e) { return false; }
                    });
            if (!validDay)
                throw new BadRequestException(
                        "Shifokor bu kunda qabul qilmaydi. Ish kunlari belgilangan.");
        }

        // 2. Ish vaqtini tekshirish
        if (doctor.getWorkStartTime() != null && doctor.getWorkEndTime() != null) {
            java.time.LocalTime apptLocalTime = apptTime.toLocalTime();
            java.time.LocalTime start = java.time.LocalTime.parse(doctor.getWorkStartTime());
            java.time.LocalTime end   = java.time.LocalTime.parse(doctor.getWorkEndTime());
            if (apptLocalTime.isBefore(start) || apptLocalTime.isAfter(end))
                throw new BadRequestException(
                        "Shifokor ish vaqti: " + doctor.getWorkStartTime() +
                                " — " + doctor.getWorkEndTime() + ". Iltimos shu vaqt oralig'ini tanlang.");
        }

        // 3. Bir xil vaqtda boshqa navbat borligini tekshirish
        boolean alreadyBooked = appointmentRepository
                .existsByDoctorIdAndAppointmentTimeBetweenAndStatusNot(
                        doctor.getId(),
                        apptTime.minusMinutes(29),
                        apptTime.plusMinutes(29),
                        AppointmentStatus.CANCELLED);
        if (alreadyBooked)
            throw new BadRequestException(
                    "Bu vaqtda shifokor band. Iltimos boshqa vaqtni tanlang.");

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .medicalService(service)
                .appointmentTime(apptTime)
                .notes(request.getNotes())
                .status(AppointmentStatus.PENDING)
                .build();

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public void cancelAppointment(Long appointmentId, String email) {
        Patient patient = getPatientByEmail(email);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Navbat topilmadi"));
        if (!appointment.getPatient().getId().equals(patient.getId()))
            throw new SecurityException("Bu navbatni bekor qilish huquqingiz yo'q");
        if (appointment.getStatus() == AppointmentStatus.COMPLETED)
            throw new BadRequestException("Bajarilgan navbatni bekor qilib bo'lmaydi");
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    public List<String> getAvailableSlots(Long doctorId, String dateStr) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Shifokor topilmadi"));
        LocalDate date = LocalDate.parse(dateStr);
        // Ish kuni tekshiruvi
        if (doctor.getWorkingDays() != null && !doctor.getWorkingDays().isEmpty()) {
            int dayValue = date.getDayOfWeek().getValue();
            boolean isWorkingDay = doctor.getWorkingDays().stream()
                    .map(String::trim)
                    .anyMatch(d -> {
                        try { return Integer.parseInt(d) == dayValue; }
                        catch (NumberFormatException e) { return false; }
                    });
            if (!isWorkingDay) return Collections.emptyList();
        }
        // Vaqt slotlarini generatsiya qilish
        LocalTime startTime = (doctor.getWorkStartTime() != null && !doctor.getWorkStartTime().isBlank())
                ? LocalTime.parse(doctor.getWorkStartTime()) : LocalTime.of(9, 0);
        LocalTime endTime = (doctor.getWorkEndTime() != null && !doctor.getWorkEndTime().isBlank())
                ? LocalTime.parse(doctor.getWorkEndTime()) : LocalTime.of(18, 0);
        LocalTime lastSlot = endTime.minusMinutes(30);
        List<String> allSlots = new ArrayList<>();
        LocalTime cur = startTime;
        while (!cur.isAfter(lastSlot)) {
            allSlots.add(cur.toString());
            cur = cur.plusMinutes(30);
        }
        // Band vaqtlarni olib tashlash
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd   = date.atTime(23, 59, 59);
        List<Appointment> booked = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetweenAndStatusNot(
                        doctorId, dayStart, dayEnd, AppointmentStatus.CANCELLED);
        Set<String> bookedTimes = booked.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(java.util.stream.Collectors.toSet());
        LocalDateTime now = LocalDateTime.now();
        return allSlots.stream()
                .filter(slot -> !bookedTimes.contains(slot))
                .filter(slot -> LocalDateTime.of(date, LocalTime.parse(slot)).isAfter(now))
                .collect(java.util.stream.Collectors.toList());
    }
}