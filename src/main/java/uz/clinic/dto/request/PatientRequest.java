package uz.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRequest {

    @NotBlank
    private String fullName;

    @NotBlank
    private String phone;

    private String email;

    private LocalDate birthDate;

    private String address;

    private String gender;
}