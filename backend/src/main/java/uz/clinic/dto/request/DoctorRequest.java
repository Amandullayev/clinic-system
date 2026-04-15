package uz.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.clinic.enums.DoctorStatus;

@Data
public class DoctorRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String specialization;

    private String phone;
    private String licenseNumber;
    private Integer experienceYears;
    private String workingDays;
    private String workStartTime;
    private String workEndTime;
    private DoctorStatus status;
}