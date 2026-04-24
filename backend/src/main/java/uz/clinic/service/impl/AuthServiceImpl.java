package uz.clinic.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.LoginRequest;
import uz.clinic.dto.request.RegisterRequest;
import uz.clinic.dto.response.AuthResponse;
import uz.clinic.entity.OtpVerification;
import uz.clinic.entity.Patient;
import uz.clinic.entity.User;
import uz.clinic.enums.Role;
import uz.clinic.exception.BadRequestException;
import uz.clinic.repository.OtpVerificationRepository;
import uz.clinic.repository.PatientRepository;
import uz.clinic.repository.UserRepository;
import uz.clinic.security.JwtUtil;
import uz.clinic.service.AuthService;
import uz.clinic.service.EmailService;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpVerificationRepository otpRepository;
    private final EmailService emailService;
    private final PatientRepository patientRepository;
    private final SecureRandom secureRandom = new SecureRandom();


    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Email yoki parol noto'g'ri"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Email yoki parol noto'g'ri");
        }

        if (!user.isActive()) {
            throw new BadRequestException("Foydalanuvchi faol emas");
        }

        String role = "ROLE_" + user.getRole().name();
        String token = jwtUtil.generateToken(user.getEmail(), role);
        return new AuthResponse(user.getId(), token, user.getFullName(), user.getEmail(), user.getRole());
    }

    @Override
    public ApiResponse sendOtp(String email) {
        int otp = 100000 + secureRandom.nextInt(900000);
        otpRepository.save(OtpVerification.builder()
                .email(email)
                .otp(String.valueOf(otp))
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build());
        emailService.sendOtp(email, String.valueOf(otp));
        return new ApiResponse(true, "Kod emailga jo'natildi", null);
    }


    @Override
    public AuthResponse verifyOtpAndRegister(RegisterRequest request, String otp) {
        // 1. Avval OTP tekshiriladi
        OtpVerification verification = otpRepository
                .findTopByEmailOrderByIdDesc(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Kod topilmadi"));
        if (verification.isUsed())
            throw new BadRequestException("Kod allaqachon ishlatilgan");
        if (verification.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Kod muddati o'tgan");
        if (!verification.getOtp().equals(otp))
            throw new BadRequestException("Kod noto'g'ri");

        verification.setUsed(true);
        otpRepository.save(verification);

        // 2. Email tekshiriladi
        if (userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Bu email allaqachon ro'yxatdan o'tgan");

        // 3. User yaratiladi
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.PATIENT)
                .active(true)
                .build();
        User saved = userRepository.save(user);

        // 4. Avtomatik bemor yaratiladi
        Patient patient = Patient.builder()
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .user(saved)
                .build();
        patientRepository.save(patient);

        String role = "ROLE_" + saved.getRole().name();
        String token = jwtUtil.generateToken(saved.getEmail(), role);
        return new AuthResponse(saved.getId(), token, saved.getFullName(), saved.getEmail(), saved.getRole());
    }
}