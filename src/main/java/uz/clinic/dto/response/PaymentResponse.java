package uz.clinic.dto.response;

import lombok.Data;
import uz.clinic.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {

    private Long id;
    private Long appointmentId;
    private String patientName;
    private BigDecimal amount;
    private String serviceName;
    private PaymentStatus status;
    private String paymentMethod;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}