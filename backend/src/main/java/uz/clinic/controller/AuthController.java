package uz.clinic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.ChangePasswordRequest;
import uz.clinic.dto.request.LoginRequest;
import uz.clinic.dto.request.RegisterRequest;
import uz.clinic.dto.response.AuthResponse;
import uz.clinic.service.AuthService;
import uz.clinic.service.MessageService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MessageService msg;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(msg.get("auth.login.success"), authService.login(request))
        );
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse> sendOtp(@RequestParam String email) {
        return ResponseEntity.ok(authService.sendOtp(email));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @RequestBody RegisterRequest request,
            @RequestParam String otp) {
        return ResponseEntity.ok(
                ApiResponse.ok(msg.get("auth.register.success"), authService.verifyOtpAndRegister(request, otp))
        );
    }

    // QO'SHILDI: parol almashtirish endpointi
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        authService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.ok(msg.get("auth.password.changed"), null));
    }
}