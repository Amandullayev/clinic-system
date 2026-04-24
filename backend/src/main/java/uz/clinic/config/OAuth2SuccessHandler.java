package uz.clinic.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import uz.clinic.entity.User;
import uz.clinic.enums.Role;
import uz.clinic.repository.UserRepository;
import uz.clinic.security.JwtUtil;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${frontend.url}")
    private String frontendUrl;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    @Override
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
                    .password("")
                    .role(Role.PATIENT)
                    .active(true)
                    .build();
            return userRepository.save(newUser);
        });

        String role  = "ROLE_" + user.getRole().name();
        String token = jwtUtil.generateToken(user.getEmail(), role);

        response.sendRedirect(frontendUrl + "/oauth2/callback?token=" + token);
    }
}