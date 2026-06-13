package uz.clinic.mapper;

import org.springframework.stereotype.Component;
import uz.clinic.dto.response.PaymentResponse;
import uz.clinic.entity.Payment;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setAppointmentId(payment.getAppointment().getId());
        response.setPatientName(
                payment.getAppointment().getPatient().getFullName());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaidAt(payment.getPaidAt());
        response.setCreatedAt(payment.getCreatedAt());

        if (payment.getAppointment().getMedicalService() != null) {
            response.setServiceName(
                    payment.getAppointment().getMedicalService().getName());
        }

        return response;
    }
}