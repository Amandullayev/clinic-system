package uz.clinic.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.clinic.entity.Patient;
import uz.clinic.entity.User;
import uz.clinic.enums.Role;
import uz.clinic.repository.PatientRepository;
import uz.clinic.repository.UserRepository;
import uz.clinic.security.JwtUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name  = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .fullName(name)
                    .email(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .role(Role.PATIENT)
                    .active(true)
                    .build();
            User saved = userRepository.save(newUser);
            patientRepository.save(Patient.builder()
                    .fullName(name)
                    .email(email)
                    .user(saved)
                    .active(true)
                    .build());
            return saved;
        });

        String role  = "ROLE_" + user.getRole().name();
        String token = jwtUtil.generateToken(user.getEmail(), role);

        // TUZATILDI: HttpOnly cookie o'rniga token frontend kutgan query param orqali uzatiladi.
        // Frontend (OAuth2Callback.jsx) /oauth2/callback?token=... ni kutadi.
        String redirectUrl = frontendUrl + "/oauth2/callback?token="
                + java.net.URLEncoder.encode(token, StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }
}