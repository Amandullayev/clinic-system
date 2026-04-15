package uz.clinic.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.clinic.entity.User;
import uz.clinic.enums.Role;
import uz.clinic.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createIfNotExists("Super Admin",    "superadmin@cliniq.uz",    "super123",   Role.SUPER_ADMIN);
        createIfNotExists("Admin User",     "admin@cliniq.uz",         "admin123",   Role.ADMIN);
        createIfNotExists("Receptionist",   "reception@cliniq.uz",     "recept123",  Role.RECEPTIONIST);
        createIfNotExists("Dr.Alixon",     "doctor@cliniq.uz",        "doctor123",  Role.DOCTOR);
        createIfNotExists("Bemor Vali",     "patient@cliniq.uz",       "patient123", Role.PATIENT);
    }

    private void createIfNotExists(String fullName, String email, String password, Role role) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            userRepository.save(User.builder()
                    .fullName(fullName)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .active(true)
                    .build());
            System.out.println("✅ Yaratildi: " + email);
        } else {
            user.setPassword(passwordEncoder.encode(password));
            user.setActive(true);
            userRepository.save(user);
            System.out.println("🔄 Parol yangilandi: " + email);
        }
    }
}