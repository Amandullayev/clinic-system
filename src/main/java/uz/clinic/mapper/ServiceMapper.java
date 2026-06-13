package uz.clinic.mapper;

import org.springframework.stereotype.Component;
import uz.clinic.dto.request.ServiceRequest;
import uz.clinic.dto.response.ServiceResponse;
import uz.clinic.entity.MedicalService;

@Component
public class ServiceMapper {

    public MedicalService toEntity(ServiceRequest request) {
        return MedicalService.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .durationMinutes(request.getDurationMinutes())
                .category(request.getCategory())
                .build();
    }

    public ServiceResponse toResponse(MedicalService service) {
        ServiceResponse response = new ServiceResponse();
        response.setId(service.getId());
        response.setName(service.getName());
        response.setDescription(service.getDescription());
        response.setPrice(service.getPrice());
        response.setDurationMinutes(service.getDurationMinutes());
        response.setActive(service.isActive());
        response.setCategory(service.getCategory());
        return response;
    }

    public void updateEntity(MedicalService service, ServiceRequest request) {
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDurationMinutes(request.getDurationMinutes());
        service.setCategory(request.getCategory());
    }
}