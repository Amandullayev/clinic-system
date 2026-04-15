package uz.clinic.service;

import uz.clinic.dto.request.DoctorRequest;
import uz.clinic.dto.response.DoctorResponse;

import java.util.List;

public interface DoctorService {
    List<DoctorResponse> getAll();
    DoctorResponse getById(Long id);
    DoctorResponse create(DoctorRequest request);
    DoctorResponse update(Long id, DoctorRequest request);
    void delete(Long id);
}