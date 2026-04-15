package uz.clinic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.UserCreateRequest;
import uz.clinic.dto.request.UserUpdateRequest;
import uz.clinic.dto.response.UserResponse;
import uz.clinic.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Foydalanuvchi yaratildi", userService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Yangilandi", userService.update(id, request)));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<UserResponse>> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Holat o'zgartirildi", userService.toggleActive(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("O'chirildi", null));
    }
}