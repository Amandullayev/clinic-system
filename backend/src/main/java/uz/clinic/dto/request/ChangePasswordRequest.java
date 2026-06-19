package uz.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Joriy parol kiritilishi shart")
    private String oldPassword;

    @NotBlank(message = "Yangi parol kiritilishi shart")
    @Size(min = 6, message = "Yangi parol kamida 6 ta belgi bo'lishi kerak")
    private String newPassword;
}