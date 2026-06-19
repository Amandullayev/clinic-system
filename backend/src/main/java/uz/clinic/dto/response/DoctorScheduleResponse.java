package uz.clinic.dto.response;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

// YANGI DTO
@Data
public class DoctorScheduleResponse {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}