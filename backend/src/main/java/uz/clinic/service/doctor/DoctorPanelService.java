package uz.clinic.service.doctor;

import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.dto.response.PatientResponse;

import java.util.List;

public interface DoctorPanelService {

    List<AppointmentResponse> getMyAppointments(String email);

    List<PatientResponse> getMyPatients(String email);

    AppointmentResponse updateAppointmentStatus(Long appointmentId, String status, String email);
}