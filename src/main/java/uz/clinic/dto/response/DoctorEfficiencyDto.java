package uz.clinic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DoctorEfficiencyDto {
    private String doctorName;
    private long patients;
    private int workDays;
    private BigDecimal revenue;
    private BigDecimal avgPrice;
}