package uz.clinic.mapper;

import org.springframework.stereotype.Component;
import uz.clinic.dto.response.DoctorResponse;
import uz.clinic.entity.Doctor;

@Component
public class DoctorMapper {

    public DoctorResponse toResponse(Doctor doctor) {
        DoctorResponse response = new DoctorResponse();
        response.setId(doctor.getId());
        response.setFullName(doctor.getUser().getFullName());
        response.setEmail(doctor.getUser().getEmail());
        response.setSpecialization(doctor.getSpecialization());
        response.setPhone(doctor.getPhone());
        response.setLicenseNumber(doctor.getLicenseNumber());
        response.setExperienceYears(doctor.getExperienceYears());
        response.setActive(doctor.isActive());
        response.setStatus(doctor.getStatus());
        response.setRating(doctor.getRating());
        response.setWorkingDays(doctor.getWorkingDays());
        response.setWorkStartTime(doctor.getWorkStartTime());
        response.setWorkEndTime(doctor.getWorkEndTime());
        return response;
    }
}