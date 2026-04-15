package uz.clinic.service;

import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.AppointmentRequest;
import uz.clinic.dto.response.AppointmentResponse;

import java.util.List;

public interface AppointmentService {
    List<AppointmentResponse> getAll();
    AppointmentResponse getById(Long id);
    AppointmentResponse create(AppointmentRequest request);
    AppointmentResponse update(Long id, AppointmentRequest request);
    void delete(Long id);
    ApiResponse getAppointmentsByPatientEmail(String email);
}