package uz.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.AppointmentRequest;
import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.entity.*;
import uz.clinic.enums.AppointmentStatus;
import uz.clinic.exception.BadRequestException;
import uz.clinic.exception.ResourceNotFoundException;
import uz.clinic.mapper.AppointmentMapper;
import uz.clinic.repository.*;
import uz.clinic.service.AppointmentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final AppointmentMapper appointmentMapper;
    private final UserRepository userRepository;
    @Override
    public AppointmentResponse getById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Qabul topilmadi: " + id));
        return appointmentMapper.toResponse(appointment);
    }

    @Override
    public ApiResponse getAppointmentsByPatientEmail(String email) {
        User patient = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));
        List<Appointment> appointments = appointmentRepository.findByPatient(patient);
        List<AppointmentResponse> responses = appointments.stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
        return new ApiResponse(true, "Muvaffaqiyatli", responses);
    }

    @Override
    public AppointmentResponse create(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Bemor topilmadi"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Shifokor topilmadi"));

        MedicalService service = medicalServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Xizmat topilmadi"));

        if (appointmentRepository.existsByDoctorIdAndAppointmentTime(
                request.getDoctorId(), request.getAppointmentTime())) {
            throw new BadRequestException("Bu shifokor ushbu vaqtda band");
        }

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
    public AppointmentResponse update(Long id, AppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Qabul topilmadi: " + id));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Bemor topilmadi"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Shifokor topilmadi"));

        MedicalService service = medicalServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Xizmat topilmadi"));

        if (appointmentRepository.existsByDoctorIdAndAppointmentTimeAndIdNot(
                request.getDoctorId(), request.getAppointmentTime(), id)) {
            throw new BadRequestException("Bu shifokor ushbu vaqtda band");
        }

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setMedicalService(service);
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setNotes(request.getNotes());

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public void delete(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Qabul topilmadi: " + id));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    public List<AppointmentResponse> getAll() {
        return appointmentRepository.findAll()
                .stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .map(appointmentMapper::toResponse)
                .toList();
    }
}