package uz.clinic.mapper;

import org.springframework.stereotype.Component;
import uz.clinic.dto.response.DoctorResponse;
import uz.clinic.dto.response.DoctorScheduleResponse;
import uz.clinic.entity.Doctor;
import uz.clinic.entity.DoctorSchedule;

import java.util.Comparator;
import java.util.List;

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

        // O'ZGARTIRILDI: schedules ro'yxati hafta kuni bo'yicha tartiblanib qaytariladi
        response.setSchedules(toScheduleResponses(doctor.getSchedules()));

        return response;
    }

    private List<DoctorScheduleResponse> toScheduleResponses(List<DoctorSchedule> schedules) {
        return schedules.stream()
                .sorted(Comparator.comparingInt(s -> s.getDayOfWeek().getValue()))
                .map(s -> {
                    DoctorScheduleResponse dto = new DoctorScheduleResponse();
                    dto.setDayOfWeek(s.getDayOfWeek());
                    dto.setStartTime(s.getStartTime());
                    dto.setEndTime(s.getEndTime());
                    return dto;
                })
                .toList();
    }
}