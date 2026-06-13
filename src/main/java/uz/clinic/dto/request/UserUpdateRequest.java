package uz.clinic.dto.request;

import lombok.Data;
import uz.clinic.enums.Role;

@Data
public class UserUpdateRequest {
    private String fullName;
    private String email;
    private Role role;
    private Boolean active;
}