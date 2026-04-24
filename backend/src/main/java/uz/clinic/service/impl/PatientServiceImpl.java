package uz.clinic.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.dto.request.PatientRequest;
import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.dto.response.PatientResponse;
import uz.clinic.dto.response.PaymentResponse;
import uz.clinic.entity.Patient;
import uz.clinic.exception.BadRequestException;
import uz.clinic.exception.ResourceNotFoundException;
import uz.clinic.mapper.PatientMapper;
import uz.clinic.repository.PatientRepository;
import uz.clinic.service.PatientService;
import uz.clinic.dto.response.PatientDetailsResponse;
import uz.clinic.mapper.AppointmentMapper;
import uz.clinic.mapper.PaymentMapper;
import uz.clinic.repository.AppointmentRepository;
import uz.clinic.repository.PaymentRepository;

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
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bemor topilmadi: " + id));
        return patientMapper.toResponse(patient);
    }

    @Override
    public PatientDetailsResponse getDetails(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bemor topilmadi: " + id));

        PatientDetailsResponse details = new PatientDetailsResponse();
        details.setPatient(patientMapper.toResponse(patient));

        // Oxirgi 5 ta navbat
        List<AppointmentResponse> appointments = appointmentRepository
                .findAllByPatientId(id)
                .stream()
                .sorted((a, b) -> b.getAppointmentTime().compareTo(a.getAppointmentTime()))
                .limit(5)
                .map(appointmentMapper::toResponse)
                .toList();
        details.setRecentAppointments(appointments);

        // Oxirgi 3 ta to'lov
        List<PaymentResponse> payments = paymentRepository
                .findByPatientId(id)
                .stream()
                .limit(3)
                .map(paymentMapper::toResponse)
                .toList();
        details.setRecentPayments(payments);

        return details;
    }

    @Override
    public PatientResponse create(PatientRequest request) {
        if (patientRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Bu telefon raqam allaqachon mavjud");
        }
        Patient patient = patientMapper.toEntity(request);
        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    public PatientResponse update(Long id, PatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bemor topilmadi: " + id));
        patientMapper.updateEntity(patient, request);
        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    public void delete(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bemor topilmadi: " + id));
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
}