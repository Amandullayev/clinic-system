package uz.clinic.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ReportResponse {

    private long totalPatients;
    private long totalVisits;
    private BigDecimal totalRevenue;
    private BigDecimal avgPrice;

    private List<ServiceStatDto> popularServices;
    private List<MonthlyRevenueDto> monthlyRevenue;
    private List<WeekdayVisitDto> weekdayVisits;
    private List<DoctorEfficiencyDto> doctorEfficiency;
}