package uz.clinic.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

// YANGI DTO: DoctorRequest ichida ro'yxat sifatida ishlatiladi.
// Frontend har bir kun uchun shu strukturani yuboradi.
@Data
public class DoctorScheduleRequest {

    @NotNull(message = "Hafta kuni ko'rsatilishi shart")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Ish boshlanish vaqti ko'rsatilishi shart")
    private LocalTime startTime;

    @NotNull(message = "Ish tugash vaqti ko'rsatilishi shart")
    private LocalTime endTime;
}