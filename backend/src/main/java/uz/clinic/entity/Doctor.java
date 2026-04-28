package uz.clinic.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.clinic.enums.DoctorStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String specialization;

    private String phone;

    private String licenseNumber;

    private Integer experienceYears;

    @Column(nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    private DoctorStatus status;

    private Double rating;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doctor_working_days", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "day_of_week")
    private List<String> workingDays = new ArrayList<>();    // masalan: "Dush-Juma"

    private String workStartTime;    // masalan: "09:00"

    private String workEndTime;      // masalan: "18:00"

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}