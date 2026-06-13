package uz.clinic.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardResponse {

    // Yuqori statistika kartalar
    private long todayPatients;
    private long pendingAppointments;
    private BigDecimal monthlyRevenue;
    private long newPatientsToday;

    // Bugungi navbatlar ro'yxati
    private List<AppointmentResponse> todayAppointments;

    // So'nggi to'lovlar
    private List<PaymentResponse> recentPayments;

    // Kam qolgan dorilar
    private List<MedicationResponse> lowStockMedications;

    // Shifokorlar holati
    private List<DoctorResponse> doctors;
}