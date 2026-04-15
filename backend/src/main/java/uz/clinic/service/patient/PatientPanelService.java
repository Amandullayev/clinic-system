package uz.clinic.service.patient;

import uz.clinic.dto.request.AppointmentRequest;
import uz.clinic.dto.response.AppointmentResponse;

import java.util.List;

public interface PatientPanelService {

    List<AppointmentResponse> getMyAppointments(String email);

    AppointmentResponse bookAppointment(AppointmentRequest request, String email);

    void cancelAppointment(Long appointmentId, String email);
}