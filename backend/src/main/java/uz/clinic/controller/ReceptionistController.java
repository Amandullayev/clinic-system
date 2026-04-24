package uz.clinic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.clinic.common.ApiResponse;
import uz.clinic.entity.User;
import uz.clinic.enums.Role;
import uz.clinic.exception.ResourceNotFoundException;
import uz.clinic.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/receptionists")
@RequiredArgsConstructor
public class ReceptionistController {

    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> getAll() {
        List<User> receptionists = userRepository.findByRole(Role.RECEPTIONIST);
        return ResponseEntity.ok(new ApiResponse(true, "Muvaffaqiyatli", receptionists));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Xodim topilmadi"));
        userRepository.delete(user);
        return ResponseEntity.ok(new ApiResponse(true, "O'chirildi", null));
    }
}