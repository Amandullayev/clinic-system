package uz.clinic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true)
    private String phone;

    private String email;

    private LocalDate birthDate;

    private String address;

    private String gender;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDate lastVisitDate;

    @org.hibernate.annotations.Formula(
            "(SELECT COUNT(a.id) FROM appointments a WHERE a.patient_id = id AND a.status = 'COMPLETED')"
    )
    private Integer totalVisits;

    @Column(nullable = false)
    private boolean active = true;

    // YANGI: bemorning no-show hisobi.
    // Har safar NO_SHOW belgilanganda +1 qo'shiladi.
    // Admin/Receptionist buni ko'rib, kerak bo'lsa online booking'ni cheklaydi.
    @Column(nullable = false)
    @Builder.Default
    private Integer noShowCount = 0;

    // YANGI: online booking bloklanganmi.
    // noShowCount 3 dan oshsa → true → bemor faqat klinikada (receptionist orqali) yozila oladi.
    @Column(nullable = false)
    @Builder.Default
    private boolean onlineBookingBlocked = false;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

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