package uz.clinic.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.clinic.enums.PaymentStatus;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull
    private Long appointmentId;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String paymentMethod;

    private PaymentStatus status;
}