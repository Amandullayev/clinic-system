package uz.clinic.service;

import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.ChangePasswordRequest;
import uz.clinic.dto.request.LoginRequest;
import uz.clinic.dto.request.RegisterRequest;
import uz.clinic.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    ApiResponse sendOtp(String email);
    AuthResponse verifyOtpAndRegister(RegisterRequest request, String otp);

    // QO'SHILDI
    void changePassword(String email, ChangePasswordRequest request);
}