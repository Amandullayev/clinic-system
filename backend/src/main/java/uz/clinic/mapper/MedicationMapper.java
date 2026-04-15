package uz.clinic.mapper;

import org.springframework.stereotype.Component;
import uz.clinic.dto.request.MedicationRequest;
import uz.clinic.dto.response.MedicationResponse;
import uz.clinic.entity.Medication;

@Component
public class MedicationMapper {

    public Medication toEntity(MedicationRequest request) {
        return Medication.builder()
                .name(request.getName())
                .quantity(request.getQuantity())
                .minQuantity(request.getMinQuantity())
                .unit(request.getUnit())
                .build();
    }

    public MedicationResponse toResponse(Medication medication) {
        MedicationResponse response = new MedicationResponse();
        response.setId(medication.getId());
        response.setName(medication.getName());
        response.setQuantity(medication.getQuantity());
        response.setMinQuantity(medication.getMinQuantity());
        response.setUnit(medication.getUnit());
        response.setActive(medication.isActive());
        response.setLowStock(medication.getQuantity() < medication.getMinQuantity());
        return response;
    }

    public void updateEntity(Medication medication, MedicationRequest request) {
        medication.setName(request.getName());
        medication.setQuantity(request.getQuantity());
        medication.setMinQuantity(request.getMinQuantity());
        medication.setUnit(request.getUnit());
    }
}