package uz.clinic.service;

import uz.clinic.dto.request.AppointmentRequest;
import uz.clinic.dto.response.AppointmentResponse;

import java.util.List;

public interface PatientPanelService {

    List<AppointmentResponse> getMyAppointments(String email);

    AppointmentResponse bookAppointment(AppointmentRequest request, String email);

    void cancelAppointment(Long appointmentId, String email);

    List<String> getAvailableSlots(Long doctorId, String date);

    // YANGI: email havolasi orqali tasdiqlash
    AppointmentResponse confirmByToken(String token);

    // YANGI: email havolasi orqali bekor qilish
    void cancelByToken(String token);
}