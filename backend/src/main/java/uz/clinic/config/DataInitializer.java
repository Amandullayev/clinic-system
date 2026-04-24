package uz.clinic.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.clinic.entity.Doctor;
import uz.clinic.entity.Patient;
import uz.clinic.entity.User;
import uz.clinic.enums.DoctorStatus;
import uz.clinic.enums.Role;
import uz.clinic.repository.DoctorRepository;
import uz.clinic.repository.PatientRepository;
import uz.clinic.repository.UserRepository;


@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public void run(String... args) {
        createIfNotExists("Super Admin",    "superadmin@clinic.uz",    "super123",   Role.SUPER_ADMIN);
        createIfNotExists("Admin User",     "admin@clinic.uz",         "admin123",   Role.ADMIN);
        createIfNotExists("Receptionist",   "reception@clinic.uz",     "recept123",  Role.RECEPTIONIST);
        createIfNotExists("Dr.Alixon",     "doctor@clinic.uz",        "doctor123",  Role.DOCTOR);
        createIfNotExists("Bemor Vali",     "patient@clinic.uz",       "patient123", Role.PATIENT);

        // Qabulxona xodimlari — 3 ta
        createIfNotExists("Receptionist 1",  "rc1@clinic.uz",        "recept123", Role.RECEPTIONIST);
        createIfNotExists("Receptionist 2",  "rc2@clinic.uz",        "recept123", Role.RECEPTIONIST);
        createIfNotExists("Receptionist 3",  "rc3@clinic.uz",        "recept123", Role.RECEPTIONIST);
        // Bemorlar — 3 ta
        createIfNotExists("Bemor Vali",      "pt1@clinic.uz",        "patient123", Role.PATIENT);
        createIfNotExists("Bemor Aziz",      "pt2@clinic.uz",        "patient123", Role.PATIENT);
        createIfNotExists("Bemor Nilufar",   "pt3@clinic.uz",        "patient123", Role.PATIENT);
    }

    private void createIfNotExists(String fullName, String email, String password, Role role) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            User saved = userRepository.save(User.builder()
                    .fullName(fullName)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .active(true)
                    .build());
            System.out.println("✅ Yaratildi: " + email);

            if (role == Role.PATIENT) {
                if (!patientRepository.existsByEmail(email)) {
                    patientRepository.save(Patient.builder()
                            .fullName(fullName)
                            .email(email)
                            .active(true)
                            .user(saved)
                            .build());
                }
            }

            if (role == Role.DOCTOR) {
                boolean exists = doctorRepository.findAll()
                        .stream()
                        .anyMatch(d -> d.getUser() != null
                                && d.getUser().getId().equals(saved.getId()));
                if (!exists) {
                    doctorRepository.save(Doctor.builder()
                            .user(saved)
                            .specialization("Umumiy amaliyot")
                            .active(true)
                            .status(DoctorStatus.ACTIVE)
                            .experienceYears(5)
                            .workingDays("Dush-Juma")
                            .workStartTime("09:00")
                            .workEndTime("18:00")
                            .build());
                }
            }
        } else {
            System.out.println("✅ Allaqachon mavjud: " + email);
        }
    }
}