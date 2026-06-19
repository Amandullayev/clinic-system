package uz.clinic.dto.response;

import lombok.Data;
import uz.clinic.enums.DoctorStatus;

import java.util.ArrayList;
import java.util.List;

@Data
public class DoctorResponse {

    private Long id;
    private String fullName;
    private String email;
    private String specialization;
    private String phone;
    private DoctorStatus status;
    private Double rating;
    private String licenseNumber;
    private Integer experienceYears;
    private boolean active;

    // O'ZGARTIRILDI: eski workingDays / workStartTime / workEndTime o'rniga
    // har bir kun uchun alohida jadval ro'yxati.
    private List<DoctorScheduleResponse> schedules = new ArrayList<>();
}