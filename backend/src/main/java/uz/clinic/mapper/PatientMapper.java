package uz.clinic.mapper;

import org.springframework.stereotype.Component;
import uz.clinic.dto.request.PatientRequest;
import uz.clinic.dto.response.PatientResponse;
import uz.clinic.entity.Patient;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequest request) {
        return Patient.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .birthDate(request.getBirthDate())
                .address(request.getAddress())
                .gender(request.getGender())
                .build();
    }

    public PatientResponse toResponse(Patient patient) {
        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setFullName(patient.getFullName());
        response.setPhone(patient.getPhone());
        response.setEmail(patient.getEmail());
        response.setBirthDate(patient.getBirthDate());
        response.setAddress(patient.getAddress());
        response.setGender(patient.getGender());
        response.setCreatedAt(patient.getCreatedAt());
        response.setLastVisitDate(patient.getLastVisitDate());
        response.setTotalVisits(patient.getTotalVisits());
        response.setActive(patient.isActive());
        return response;
    }

    public void updateEntity(Patient patient, PatientRequest request) {
        patient.setFullName(request.getFullName());
        patient.setPhone(request.getPhone());
        patient.setEmail(request.getEmail());
        patient.setBirthDate(request.getBirthDate());
        patient.setAddress(request.getAddress());
        patient.setGender(request.getGender());
    }
}