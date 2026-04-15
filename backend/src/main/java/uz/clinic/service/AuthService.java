package uz.clinic.service;

import uz.clinic.dto.request.LoginRequest;
import uz.clinic.dto.request.RegisterRequest;
import uz.clinic.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}