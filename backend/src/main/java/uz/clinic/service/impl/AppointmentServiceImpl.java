package uz.clinic.service.impl;

import lombok.RequiredArgsConstructor;
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
import uz.clinic.service.AppointmentService;
import uz.clinic.service.AppointmentValidator;
import uz.clinic.service.MessageService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository    appointmentRepository;
    private final PatientRepository        patientRepository;
    private final DoctorRepository         doctorRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final AppointmentMapper        appointmentMapper;
    private final AppointmentValidator     appointmentValidator;
    private final MessageService           messageService;

    @Override
    public List<AppointmentResponse> getAppointmentsByPatientEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorType.PATIENT_NOT_FOUND));
        return appointmentRepository.findByPatient(patient)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    public AppointmentResponse getById(Long id) {
        return appointmentMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public AppointmentResponse create(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new AppException(ErrorType.PATIENT_NOT_FOUND));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new AppException(ErrorType.DOCTOR_NOT_FOUND));

        MedicalService service = medicalServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorType.SERVICE_NOT_FOUND));

        appointmentValidator.validate(doctor, request.getAppointmentTime(), null);

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .medicalService(service)
                .appointmentTime(request.getAppointmentTime())
                .notes(request.getNotes())
                .build();

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse update(Long id, AppointmentRequest request) {
        Appointment appointment = findById(id);

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new AppException(ErrorType.PATIENT_NOT_FOUND));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new AppException(ErrorType.DOCTOR_NOT_FOUND));

        MedicalService service = medicalServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorType.SERVICE_NOT_FOUND));

        appointmentValidator.validate(doctor, request.getAppointmentTime(), id);

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setMedicalService(service);
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setNotes(request.getNotes());

        if (AppointmentStatus.COMPLETED.equals(request.getStatus())) {
            appointment.setStatus(AppointmentStatus.COMPLETED);
            patient.setLastVisitDate(appointment.getAppointmentTime().toLocalDate());
            patientRepository.save(patient);
        }

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = findById(id);
        appointment.setStatus(status);

        if (status == AppointmentStatus.COMPLETED) {
            Patient patient = appointment.getPatient();
            patient.setLastVisitDate(appointment.getAppointmentTime().toLocalDate());
            patientRepository.save(patient);
        }

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public void delete(Long id) {
        Appointment appointment = findById(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    public List<AppointmentResponse> getAll() {
        return appointmentRepository.findAllByStatusNot(AppointmentStatus.CANCELLED)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentResponse markArrived(Long id) {
        Appointment appointment = findById(id);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == AppointmentStatus.AUTO_CANCELLED) {
            throw new AppException(ErrorType.APPOINTMENT_STATUS_INVALID);
        }

        appointment.setStatus(AppointmentStatus.ARRIVED);
        appointment.setArrivedAt(LocalDateTime.now());
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public List<AppointmentResponse> getTodayQueue(Long doctorId) {
        LocalDateTime dayStart = LocalDate.now().atStartOfDay();
        LocalDateTime dayEnd   = dayStart.plusDays(1);
        return appointmentRepository
                .findArrivedByDoctorToday(doctorId, dayStart, dayEnd)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    private Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorType.APPOINTMENT_NOT_FOUND));
    }
}