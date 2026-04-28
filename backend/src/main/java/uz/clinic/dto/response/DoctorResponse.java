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
    private List<String> workingDays = new ArrayList<>();
    private String workStartTime;
    private String workEndTime;
    private String licenseNumber;
    private Integer experienceYears;
    private boolean active;
}