package uz.clinic.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.dto.request.PatientRequest;
import uz.clinic.dto.response.PatientResponse;
import uz.clinic.entity.Patient;
import uz.clinic.exception.BadRequestException;
import uz.clinic.exception.ResourceNotFoundException;
import uz.clinic.mapper.PatientMapper;
import uz.clinic.repository.PatientRepository;
import uz.clinic.service.PatientService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    public PatientResponse getById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bemor topilmadi: " + id));
        return patientMapper.toResponse(patient);
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