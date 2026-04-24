package uz.clinic.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class PatientDetailsResponse {
    private PatientResponse patient;
    private List<AppointmentResponse> recentAppointments;
    private List<PaymentResponse> recentPayments;
}