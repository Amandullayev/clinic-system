package uz.clinic.service.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.dto.response.PatientResponse;
import uz.clinic.entity.Appointment;
import uz.clinic.entity.Doctor;
import uz.clinic.enums.AppointmentStatus;
import uz.clinic.exception.ResourceNotFoundException;
import uz.clinic.mapper.AppointmentMapper;
import uz.clinic.mapper.PatientMapper;
import uz.clinic.repository.AppointmentRepository;
import uz.clinic.repository.DoctorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorPanelServiceImpl implements DoctorPanelService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final PatientMapper patientMapper;

    @Override
    public List<AppointmentResponse> getMyAppointments(String email) {
        Doctor doctor = getDoctorByEmail(email);
        return appointmentRepository.findAllByDoctorId(doctor.getId())
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    public List<PatientResponse> getMyPatients(String email) {
        Doctor doctor = getDoctorByEmail(email);
        return appointmentRepository.findAllByDoctorId(doctor.getId())
                .stream()
                .map(Appointment::getPatient)
                .distinct()
                .map(patientMapper::toResponse)
                .toList();
    }

    @Override
    public AppointmentResponse updateAppointmentStatus(Long appointmentId, String status, String email) {
        Doctor doctor = getDoctorByEmail(email);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Uchrashuv topilmadi"));
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new SecurityException("Bu uchrashuvni o'zgartirish huquqingiz yo'q");
        }
        appointment.setStatus(AppointmentStatus.valueOf(status.toUpperCase()));
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    private Doctor getDoctorByEmail(String email) {
        return doctorRepository.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Shifokor topilmadi"));
    }
}