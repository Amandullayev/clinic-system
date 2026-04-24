package uz.clinic.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.clinic.enums.MedicationUnit;

import java.math.BigDecimal;

@Data
public class MedicationRequest {

    @NotBlank
    private String name;

    private String category;

    @NotNull
    @Min(0)
    private Integer quantity;

    @NotNull
    @Min(1)
    private Integer minQuantity;

    @NotNull
    private MedicationUnit unit;

    @NotNull
    @Min(0)
    private BigDecimal price;
}