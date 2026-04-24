package uz.clinic.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.dto.request.MedicationRequest;
import uz.clinic.dto.response.MedicationResponse;
import uz.clinic.entity.Medication;
import uz.clinic.exception.ResourceNotFoundException;
import uz.clinic.mapper.MedicationMapper;
import uz.clinic.repository.MedicationRepository;
import uz.clinic.service.MedicationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;

    @Override
    public List<MedicationResponse> getAll() {
        return medicationRepository.findAllByActiveTrue()
                .stream()
                .map(medicationMapper::toResponse)
                .toList();
    }

    @Override
    public List<MedicationResponse> getLowStock() {
        return medicationRepository.findLowStockMedications()
                .stream()
                .map(medicationMapper::toResponse)
                .toList();
    }

    @Override
    public MedicationResponse getById(Long id) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dori topilmadi: " + id));
        return medicationMapper.toResponse(medication);
    }

    @Override
    public MedicationResponse create(MedicationRequest request) {
        Medication medication = medicationMapper.toEntity(request);
        medication.setActive(true);
        Medication saved = medicationRepository.save(medication);
        MedicationResponse response = medicationMapper.toResponse(saved);
        response.setLowStock(saved.getQuantity() < saved.getMinQuantity());
        return response;
    }

    @Override
    public MedicationResponse update(Long id, MedicationRequest request) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dori topilmadi: " + id));
        medicationMapper.updateEntity(medication, request);
        return medicationMapper.toResponse(medicationRepository.save(medication));
    }

    @Override
    public void delete(Long id) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dori topilmadi: " + id));
        medication.setActive(false);
        medicationRepository.save(medication);
    }
}