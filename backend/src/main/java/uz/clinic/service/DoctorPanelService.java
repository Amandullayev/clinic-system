package uz.clinic.service;

import uz.clinic.dto.request.DiagnoseRequest;
import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.dto.response.PatientResponse;

import java.util.List;

public interface DoctorPanelService {

    List<AppointmentResponse> getMyAppointments(String email);

    List<PatientResponse> getMyPatients(String email);

    AppointmentResponse updateAppointmentStatus(Long appointmentId, String status, String email);

    AppointmentResponse writeDiagnosis(Long appointmentId, DiagnoseRequest request, String email);
}