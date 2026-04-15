package uz.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.clinic.enums.ServiceCategory;

import java.math.BigDecimal;

@Data
public class ServiceRequest {
    private ServiceCategory category;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BigDecimal price;

    private Integer durationMinutes;
}