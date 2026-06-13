package uz.clinic.dto.response;

import lombok.Data;
import uz.clinic.enums.DoctorStatus;

@Data
public class DoctorResponse {

    private Long id;
    private String fullName;
    private String email;
    private String specialization;
    private String phone;
    private DoctorStatus status;
    private Double rating;
    private String workingDays;
    private String workStartTime;
    private String workEndTime;
    private String licenseNumber;
    private Integer experienceYears;
    private boolean active;
}