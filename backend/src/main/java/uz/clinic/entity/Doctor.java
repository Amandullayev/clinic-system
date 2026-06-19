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

    // O'ZGARTIRILDI: eski workingDays / workStartTime / workEndTime maydonlari olib tashlandi.
    // Endi har bir kun uchun alohida ish vaqti DoctorSchedule orqali saqlanadi —
    // shifokor haftaning turli kunlarida turli soatlarda ishlashi mumkin
    // (masalan: Dush-Chor 09:00-13:00, Pay-Juma 09:00-18:00).
    @Builder.Default
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DoctorSchedule> schedules = new ArrayList<>();

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

    // Yordamchi metod: berilgan kun va vaqt shifokorning ish vaqtiga to'g'ri kelishini tekshiradi
    public boolean isWorkingAt(java.time.DayOfWeek day, java.time.LocalTime time) {
        return schedules.stream()
                .filter(s -> s.getDayOfWeek() == day)
                .anyMatch(s -> !time.isBefore(s.getStartTime()) && time.isBefore(s.getEndTime()));
    }

    // Yordamchi metod: berilgan kun uchun jadval (agar mavjud bo'lmasa, null)
    public DoctorSchedule getScheduleFor(java.time.DayOfWeek day) {
        return schedules.stream()
                .filter(s -> s.getDayOfWeek() == day)
                .findFirst()
                .orElse(null);
    }
}