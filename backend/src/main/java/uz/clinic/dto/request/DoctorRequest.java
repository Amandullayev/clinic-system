package uz.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.clinic.enums.DoctorStatus;

import java.util.ArrayList;
import java.util.List;

@Data
public class DoctorRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String specialization;

    private String phone;
    private String licenseNumber;
    private Integer experienceYears;
    private List<String> workingDays = new ArrayList<>();
    private String workStartTime;
    private String workEndTime;
    private DoctorStatus status;
}