package uz.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.clinic.dto.request.PatientRequest;
import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.dto.response.PatientDetailsResponse;
import uz.clinic.dto.response.PatientResponse;
import uz.clinic.dto.response.PaymentResponse;
import uz.clinic.entity.Patient;
import uz.clinic.enums.errors.ErrorType;
import uz.clinic.exception.AppException;
import uz.clinic.mapper.AppointmentMapper;
import uz.clinic.mapper.PatientMapper;
import uz.clinic.mapper.PaymentMapper;
import uz.clinic.repository.AppointmentRepository;
import uz.clinic.repository.PatientRepository;
import uz.clinic.repository.PaymentRepository;
import uz.clinic.service.PatientService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final AppointmentMapper appointmentMapper;
    private final PaymentMapper paymentMapper;
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    public PatientResponse getById(Long id) {
        return patientMapper.toResponse(findById(id));
    }

    @Override
    public PatientDetailsResponse getDetails(Long id) {
        Patient patient = findById(id);

        PatientDetailsResponse details = new PatientDetailsResponse();
        details.setPatient(patientMapper.toResponse(patient));

        List<AppointmentResponse> appointments = appointmentRepository
                .findTop5ByPatientIdOrderByAppointmentTimeDesc(id)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
        details.setRecentAppointments(appointments);

        List<PaymentResponse> payments = paymentRepository
                .findByPatientId(id, PageRequest.of(0, 3, Sort.by("createdAt").descending()))
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
        details.setRecentPayments(payments);

        return details;
    }

    @Override
    public PatientResponse create(PatientRequest request) {
        if (patientRepository.existsByPhone(request.getPhone()))
            throw new AppException(ErrorType.PATIENT_PHONE_DUPLICATE);

        return patientMapper.toResponse(patientRepository.save(patientMapper.toEntity(request)));
    }

    @Override
    public PatientResponse update(Long id, PatientRequest request) {
        Patient patient = findById(id);
        patientMapper.updateEntity(patient, request);
        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    public void delete(Long id) {
        Patient patient = findById(id);
        patient.setActive(false);
        patientRepository.save(patient);
    }

    @Override
    public List<PatientResponse> getAll() {
        return patientRepository.findAll()
                .stream()
                .filter(Patient::isActive)
                .map(patientMapper::toResponse)
                .toList();
    }

    private Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorType.PATIENT_NOT_FOUND));
    }
}