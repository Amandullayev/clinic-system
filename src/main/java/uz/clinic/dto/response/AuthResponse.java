package uz.clinic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import uz.clinic.enums.Role;

@Data
@AllArgsConstructor
public class AuthResponse {

    private Long id;
    private String token;
    private String fullName;
    private String email;
    private Role role;
}