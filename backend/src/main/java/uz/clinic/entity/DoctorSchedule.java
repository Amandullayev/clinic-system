package uz.clinic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

// YANGI ENTITY: shifokorning har bir hafta kuni uchun alohida ish vaqti.
// Bitta Doctor bir nechta DoctorSchedule yozuviga ega bo'ladi —
// masalan: MONDAY 09:00-13:00, THURSDAY 09:00-18:00 va h.k.
// Agar biror kun uchun yozuv bo'lmasa — shifokor o'sha kuni ishlamaydi (dam olish kuni).
@Entity
@Table(
        name = "doctor_schedules",
        uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "day_of_week"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;   // MONDAY, TUESDAY, ... SUNDAY

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;   // masalan 09:00

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;     // masalan 13:00 yoki 18:00

    // Validatsiya: endTime > startTime bo'lishi kerak (servis qatlamida tekshiriladi)
}