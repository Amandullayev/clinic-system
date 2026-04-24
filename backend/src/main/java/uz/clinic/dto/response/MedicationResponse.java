package uz.clinic.dto.response;

import lombok.Data;
import uz.clinic.enums.MedicationUnit;

import java.math.BigDecimal;

@Data
public class MedicationResponse {

    private Long id;
    private String name;
    private String category;
    private Integer quantity;
    private Integer minQuantity;
    private MedicationUnit unit;
    private BigDecimal price;
    private boolean active;
    private boolean lowStock;
}