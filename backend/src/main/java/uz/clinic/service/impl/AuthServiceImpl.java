package uz.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.ChangePasswordRequest;
import uz.clinic.dto.request.LoginRequest;
import uz.clinic.dto.request.RegisterRequest;
import uz.clinic.dto.response.AuthResponse;
import uz.clinic.entity.OtpVerification;
import uz.clinic.entity.Patient;
import uz.clinic.entity.User;
import uz.clinic.enums.errors.ErrorType;
import uz.clinic.enums.Role;
import uz.clinic.exception.AppException;
import uz.clinic.repository.OtpVerificationRepository;
import uz.clinic.repository.PatientRepository;
import uz.clinic.repository.UserRepository;
import uz.clinic.security.JwtUtil;
import uz.clinic.service.AuthService;
import uz.clinic.service.EmailService;
import uz.clinic.service.MessageService;

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
    private final MessageService messageService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorType.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new AppException(ErrorType.INVALID_CREDENTIALS);

        if (!user.isActive())
            throw new AppException(ErrorType.USER_INACTIVE);

        String role = "ROLE_" + user.getRole().name();
        String token = jwtUtil.generateToken(user.getEmail(), role);
        return new AuthResponse(user.getId(), token, user.getFullName(), user.getEmail(), user.getRole());
    }

    @Override
    public ApiResponse sendOtp(String email) {
        int otp = 10000 + secureRandom.nextInt(90000);
        otpRepository.save(OtpVerification.builder()
                .email(email)
                .otp(String.valueOf(otp))
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build());
        emailService.sendOtp(email, String.valueOf(otp));
        return new ApiResponse(true, messageService.get("auth.otp.sent"), null);
    }

    @Override
    @Transactional
    public AuthResponse verifyOtpAndRegister(RegisterRequest request, String otp) {
        OtpVerification verification = otpRepository
                .findTopByEmailOrderByIdDesc(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorType.OTP_NOT_FOUND));

        if (verification.isUsed())
            throw new AppException(ErrorType.OTP_USED);
        if (verification.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new AppException(ErrorType.OTP_EXPIRED);
        if (!verification.getOtp().equals(otp))
            throw new AppException(ErrorType.OTP_INVALID);

        verification.setUsed(true);
        otpRepository.save(verification);

        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorType.EMAIL_ALREADY_EXISTS);

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.PATIENT)
                .active(true)
                .build();
        User saved = userRepository.save(user);

        patientRepository.save(Patient.builder()
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .user(saved)
                .build());

        String role = "ROLE_" + saved.getRole().name();
        String token = jwtUtil.generateToken(saved.getEmail(), role);
        return new AuthResponse(saved.getId(), token, saved.getFullName(), saved.getEmail(), saved.getRole());
    }

    // QO'SHILDI: parol almashtirish
    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorType.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new AppException(ErrorType.INVALID_CREDENTIALS);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}