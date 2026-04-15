package uz.clinic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MonthlyRevenueDto {
    private String month;
    private BigDecimal amount;
}