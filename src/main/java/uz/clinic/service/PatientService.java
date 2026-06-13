package uz.clinic.service;

import uz.clinic.dto.request.PatientRequest;
import uz.clinic.dto.response.PatientResponse;

import java.util.List;

public interface PatientService {
    List<PatientResponse> getAll();
    PatientResponse getById(Long id);
    PatientResponse create(PatientRequest request);
    PatientResponse update(Long id, PatientRequest request);
    void delete(Long id);
}