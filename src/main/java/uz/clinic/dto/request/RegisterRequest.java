package uz.clinic.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import uz.clinic.enums.Role;

@Data
public class RegisterRequest {

    @NotBlank(message = "Ism familiya kiritilishi shart")
    private String fullName;

    @Email(message = "Email noto'g'ri formatda")
    @NotBlank(message = "Email kiritilishi shart")
    private String email;

    @NotBlank(message = "Parol kiritilishi shart")
    @Size(min = 6, message = "Parol kamida 8 ta belgi bo'lishi kerak")
    private String password;

    private Role role;
}