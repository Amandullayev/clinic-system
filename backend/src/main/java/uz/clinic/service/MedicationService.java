package uz.clinic.service;

import uz.clinic.dto.request.MedicationRequest;
import uz.clinic.dto.response.MedicationResponse;

import java.util.List;

public interface MedicationService {
    List<MedicationResponse> getAll();
    List<MedicationResponse> getLowStock();
    MedicationResponse getById(Long id);
    MedicationResponse create(MedicationRequest request);
    MedicationResponse update(Long id, MedicationRequest request);
    void delete(Long id);
}