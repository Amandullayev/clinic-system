package uz.clinic.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PatientResponse {

    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private LocalDate lastVisitDate;
    private Integer totalVisits;
    private boolean active;
    private LocalDate birthDate;
    private String address;
    private String gender;
    private LocalDateTime createdAt;
}