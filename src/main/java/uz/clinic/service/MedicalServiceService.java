package uz.clinic.service;

import uz.clinic.dto.request.ServiceRequest;
import uz.clinic.dto.response.ServiceResponse;

import java.util.List;

public interface MedicalServiceService {
    List<ServiceResponse> getAll();
    ServiceResponse getById(Long id);
    ServiceResponse create(ServiceRequest request);
    ServiceResponse update(Long id, ServiceRequest request);
    void delete(Long id);
}