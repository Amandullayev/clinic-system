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