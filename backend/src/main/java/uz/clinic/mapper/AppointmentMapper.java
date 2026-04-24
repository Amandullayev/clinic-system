package uz.clinic.mapper;

import org.springframework.stereotype.Component;
import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.entity.Appointment;

@Component
public class AppointmentMapper {

    public AppointmentResponse toResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setPatientName(appointment.getPatient().getFullName());
        response.setDoctorName(appointment.getDoctor().getUser().getFullName());
        response.setServiceName(appointment.getMedicalService().getName());
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setStatus(appointment.getStatus());
        response.setNotes(appointment.getNotes());
        response.setDiagnosis(appointment.getDiagnosis());
        response.setPrescription(appointment.getPrescription());
        response.setCreatedAt(appointment.getCreatedAt());

        return response;
    }
}