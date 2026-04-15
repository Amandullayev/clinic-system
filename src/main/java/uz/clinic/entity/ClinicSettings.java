package uz.clinic.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clinic_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicSettings {

    @Id
    private Long id = 1L;  // Har doim bitta qator bo'ladi

    private String clinicName;
    private String phone;
    private String email;
    private String website;
    private String address;
    private String openTime;
    private String closeTime;
}