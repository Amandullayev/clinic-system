package uz.clinic.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.clinic.entity.Doctor;
import uz.clinic.entity.DoctorSchedule;
import uz.clinic.entity.Patient;
import uz.clinic.entity.User;
import uz.clinic.enums.DoctorStatus;
import uz.clinic.enums.Role;
import uz.clinic.repository.DoctorRepository;
import uz.clinic.repository.PatientRepository;
import uz.clinic.repository.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    // TUZATILDI: Parollar endi application.properties / environment variable dan o'qiladi
    // Misol: app.init.superadmin-password=YourStrongPass123!
    @Value("${app.init.superadmin-password}")
    private String superAdminPassword;

    @Value("${app.init.admin-password}")
    private String adminPassword;

    @Value("${app.init.receptionist-password}")
    private String receptionistPassword;

    @Value("${app.init.doctor-password}")
    private String doctorPassword;

    @Value("${app.init.patient-password}")
    private String patientPassword;

    @Override
    public void run(String... args) {
        createIfNotExists("Super Admin",    "superadmin@clinic.uz",  superAdminPassword,    Role.SUPER_ADMIN);
        createIfNotExists("Admin User",     "admin@clinic.uz",       adminPassword,         Role.ADMIN);
        createIfNotExists("Receptionist",   "reception@clinic.uz",   receptionistPassword,  Role.RECEPTIONIST);
        createIfNotExists("Dr.Alixon",      "doctor@clinic.uz",      doctorPassword,        Role.DOCTOR);
        createIfNotExists("Bemor Vali",     "patient@clinic.uz",     patientPassword,       Role.PATIENT);

        // Qabulxona xodimlari
        createIfNotExists("Receptionist 1", "rc1@clinic.uz",  receptionistPassword, Role.RECEPTIONIST);
        createIfNotExists("Receptionist 2", "rc2@clinic.uz",  receptionistPassword, Role.RECEPTIONIST);
        createIfNotExists("Receptionist 3", "rc3@clinic.uz",  receptionistPassword, Role.RECEPTIONIST);

        // Bemorlar
        createIfNotExists("Bemor Vali",    "pt1@clinic.uz", patientPassword, Role.PATIENT);
        createIfNotExists("Bemor Aziz",    "pt2@clinic.uz", patientPassword, Role.PATIENT);
        createIfNotExists("Bemor Nilufar", "pt3@clinic.uz", patientPassword, Role.PATIENT);
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
                if (!doctorRepository.existsByUserId(saved.getId())) {
                    Doctor doctor = Doctor.builder()
                            .user(saved)
                            .specialization("Umumiy amaliyot")
                            .active(true)
                            .status(DoctorStatus.ACTIVE)
                            .experienceYears(5)
                            .build();

                    // O'ZGARTIRILDI: workingDays/workStartTime/workEndTime o'rniga
                    // Dushanba-Juma 09:00-18:00 jadval sifatida qo'shiladi
                    for (DayOfWeek day : List.of(
                            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
                        doctor.getSchedules().add(DoctorSchedule.builder()
                                .doctor(doctor)
                                .dayOfWeek(day)
                                .startTime(LocalTime.of(9, 0))
                                .endTime(LocalTime.of(18, 0))
                                .build());
                    }

                    doctorRepository.save(doctor);
                }
            }
        } else {
            System.out.println("✅ Allaqachon mavjud: " + email);
        }
    }
}