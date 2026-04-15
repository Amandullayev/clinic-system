package uz.clinic.dto.response;

import lombok.Data;
import uz.clinic.enums.Role;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
}