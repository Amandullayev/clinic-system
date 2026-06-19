package uz.clinic.service;

import uz.clinic.dto.request.AppointmentRequest;
import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.enums.AppointmentStatus;

import java.util.List;

public interface AppointmentService {
    List<AppointmentResponse> getAppointmentsByPatientEmail(String email);
    List<AppointmentResponse> getAll();
    AppointmentResponse getById(Long id);
    AppointmentResponse create(AppointmentRequest request);
    AppointmentResponse update(Long id, AppointmentRequest request);
    AppointmentResponse updateStatus(Long id, AppointmentStatus status);
    void delete(Long id);
    AppointmentResponse markArrived(Long id);
    List<AppointmentResponse> getTodayQueue(Long doctorId);
}