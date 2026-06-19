package uz.clinic.enums.errors;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessType {

    // ─── Auth ────────────────────────────────────────────────────────────────
    LOGIN_SUCCESS("auth.login.success",                       HttpStatus.OK),
    LOGOUT_SUCCESS("auth.logout.success",                     HttpStatus.OK),
    OTP_SENT("auth.otp.sent",                                 HttpStatus.OK),
    REGISTER_SUCCESS("auth.register.success",                 HttpStatus.CREATED),

    // ─── User ────────────────────────────────────────────────────────────────
    USER_CREATED("user.created",                              HttpStatus.CREATED),
    USER_UPDATED("user.updated",                              HttpStatus.OK),
    USER_DELETED("user.deleted",                              HttpStatus.OK),
    USER_ACTIVATED("user.activated",                          HttpStatus.OK),
    USER_DEACTIVATED("user.deactivated",                      HttpStatus.OK),
    USER_GET_ALL("user.get.success",                          HttpStatus.OK),
    USER_GET_BY_ID("user.get_by_id.success",                  HttpStatus.OK),

    // ─── Patient ─────────────────────────────────────────────────────────────
    PATIENT_CREATED("patient.created",                        HttpStatus.CREATED),
    PATIENT_UPDATED("patient.updated",                        HttpStatus.OK),
    PATIENT_DELETED("patient.deleted",                        HttpStatus.OK),
    PATIENT_GET_ALL("patient.get.success",                    HttpStatus.OK),
    PATIENT_GET_BY_ID("patient.get_by_id.success",            HttpStatus.OK),
    PATIENT_DETAILS("patient.details.success",                HttpStatus.OK),

    // ─── Doctor ──────────────────────────────────────────────────────────────
    DOCTOR_CREATED("doctor.created",                          HttpStatus.CREATED),
    DOCTOR_UPDATED("doctor.updated",                          HttpStatus.OK),
    DOCTOR_DELETED("doctor.deleted",                          HttpStatus.OK),
    DOCTOR_GET_ALL("doctor.get.success",                      HttpStatus.OK),
    DOCTOR_GET_BY_ID("doctor.get_by_id.success",              HttpStatus.OK),

    // ─── Appointment ─────────────────────────────────────────────────────────
    APPOINTMENT_CREATED("appointment.created",                HttpStatus.CREATED),
    APPOINTMENT_UPDATED("appointment.updated",                HttpStatus.OK),
    APPOINTMENT_CANCELLED("appointment.cancelled",            HttpStatus.OK),
    APPOINTMENT_GET_ALL("appointment.get.success",            HttpStatus.OK),
    APPOINTMENT_GET_BY_ID("appointment.get_by_id.success",    HttpStatus.OK),

    // ─── Payment ─────────────────────────────────────────────────────────────
    PAYMENT_CREATED("payment.created",                        HttpStatus.CREATED),
    PAYMENT_UPDATED("payment.updated",                        HttpStatus.OK),
    PAYMENT_REFUNDED("payment.refunded",                      HttpStatus.OK),
    PAYMENT_GET_ALL("payment.get.success",                    HttpStatus.OK),
    PAYMENT_GET_BY_ID("payment.get_by_id.success",            HttpStatus.OK),

    // ─── Medical Service ─────────────────────────────────────────────────────
    SERVICE_CREATED("service.created",                        HttpStatus.CREATED),
    SERVICE_UPDATED("service.updated",                        HttpStatus.OK),
    SERVICE_DELETED("service.deleted",                        HttpStatus.OK),
    SERVICE_GET_ALL("service.get.success",                    HttpStatus.OK),
    SERVICE_GET_BY_ID("service.get_by_id.success",            HttpStatus.OK),

    // ─── Medication ──────────────────────────────────────────────────────────
    MEDICATION_CREATED("medication.created",                  HttpStatus.CREATED),
    MEDICATION_UPDATED("medication.updated",                  HttpStatus.OK),
    MEDICATION_DELETED("medication.deleted",                  HttpStatus.OK),
    MEDICATION_GET_ALL("medication.get.success",              HttpStatus.OK),
    MEDICATION_GET_BY_ID("medication.get_by_id.success",      HttpStatus.OK),

    // ─── Receptionist ────────────────────────────────────────────────────────
    RECEPTIONIST_GET_ALL("receptionist.get.success",          HttpStatus.OK),
    RECEPTIONIST_DELETED("receptionist.deleted",              HttpStatus.OK),

    // ─── Settings ────────────────────────────────────────────────────────────
    SETTINGS_GET("settings.get.success",                      HttpStatus.OK),
    SETTINGS_UPDATED("settings.updated",                      HttpStatus.OK),

    // ─── Report ──────────────────────────────────────────────────────────────
    REPORT_GET("report.get.success",                          HttpStatus.OK),

    // ─── Dashboard ───────────────────────────────────────────────────────────
    DASHBOARD_GET("dashboard.get.success",                    HttpStatus.OK),

    // ─── General ─────────────────────────────────────────────────────────────
    SUCCESS("general.success",                                HttpStatus.OK);

    private final String messageKey;
    private final HttpStatus status;

    SuccessType(String messageKey, HttpStatus status) {
        this.messageKey = messageKey;
        this.status     = status;
    }
}
