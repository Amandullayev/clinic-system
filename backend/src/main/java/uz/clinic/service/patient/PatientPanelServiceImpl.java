package uz.clinic.service.patient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.dto.request.AppointmentRequest;
import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.entity.Appointment;
import uz.clinic.entity.Doctor;
import uz.clinic.entity.MedicalService;
import uz.clinic.entity.Patient;
import uz.clinic.enums.AppointmentStatus;
import uz.clinic.exception.ResourceNotFoundException;
import uz.clinic.mapper.AppointmentMapper;
import uz.clinic.repository.AppointmentRepository;
import uz.clinic.repository.DoctorRepository;
import uz.clinic.repository.MedicalServiceRepository;
import uz.clinic.repository.PatientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientPanelServiceImpl implements PatientPanelService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final AppointmentMapper appointmentMapper;

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

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setMedicalService(service);
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(AppointmentStatus.PENDING);

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public void cancelAppointment(Long appointmentId, String email) {
        Patient patient = getPatientByEmail(email);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Uchrashuv topilmadi"));
        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new SecurityException("Bu uchrashuvni bekor qilish huquqingiz yo'q");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    private Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Bemor topilmadi"));
    }
}