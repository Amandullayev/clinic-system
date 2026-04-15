package uz.clinic.dto.response;

import lombok.Data;
import uz.clinic.enums.ServiceCategory;

import java.math.BigDecimal;

@Data
public class ServiceResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationMinutes;
    private boolean active;
    private ServiceCategory category;
}