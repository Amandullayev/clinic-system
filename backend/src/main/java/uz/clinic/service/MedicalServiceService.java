package uz.clinic.service;

import uz.clinic.dto.request.ServiceRequest;
import uz.clinic.dto.response.ServiceResponse;
import uz.clinic.enums.ServiceCategory;

import java.util.List;

public interface MedicalServiceService {
    // BUG #3 TUZATILDI: category parametri qo'shildi
    List<ServiceResponse> getAll(ServiceCategory category);
    ServiceResponse getById(Long id);
    ServiceResponse create(ServiceRequest request);
    ServiceResponse update(Long id, ServiceRequest request);
    void delete(Long id);
}
