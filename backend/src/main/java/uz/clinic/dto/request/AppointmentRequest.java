package uz.clinic.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.clinic.enums.AppointmentStatus;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {

    private Long patientId;

    @NotNull
    private Long doctorId;

    @NotNull
    private Long serviceId;

    @NotNull
    private LocalDateTime appointmentTime;

    private String notes;

    private AppointmentStatus status;
}