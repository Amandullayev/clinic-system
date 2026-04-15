package uz.clinic.dto.response;

import lombok.Data;

@Data
public class MedicationResponse {

    private Long id;
    private String name;
    private Integer quantity;
    private Integer minQuantity;
    private String unit;
    private boolean active;
    private boolean lowStock;  // quantity < minQuantity bo'lsa true
}