package uz.clinic.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.clinic.dto.request.LoginRequest;
import uz.clinic.dto.request.RegisterRequest;
import uz.clinic.dto.response.AuthResponse;
import uz.clinic.entity.User;
import uz.clinic.enums.Role;
import uz.clinic.exception.BadRequestException;
import uz.clinic.repository.UserRepository;
import uz.clinic.security.JwtUtil;
import uz.clinic.service.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Bu email allaqachon ro'yxatdan o'tgan");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.PATIENT)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);
        String role = "ROLE_" + savedUser.getRole().name();
        String token = jwtUtil.generateToken(savedUser.getEmail(), role);
        return new AuthResponse(savedUser.getId(), token, savedUser.getFullName(), savedUser.getEmail(), savedUser.getRole());
    }
}