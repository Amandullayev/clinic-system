package uz.clinic.dto.response;

import lombok.Data;
import uz.clinic.enums.AppointmentStatus;

import java.time.LocalDateTime;

@Data
public class AppointmentResponse {

    private Long id;
    private String patientName;
    private String doctorName;
    private String serviceName;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private String notes;
    private String diagnosis;
    private String prescription;

    // YANGI: tasdiqlash va kelish vaqtlari
    private LocalDateTime confirmedAt;
    private LocalDateTime arrivedAt;

    // YANGI: navbat raqami
    private Integer queueNumber;

    private LocalDateTime createdAt;
}