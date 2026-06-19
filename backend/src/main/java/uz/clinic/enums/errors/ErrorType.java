package uz.clinic.enums.errors;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {

    // ─── Auth ────────────────────────────────────────────────────────────────
    INVALID_CREDENTIALS("auth.invalid_credentials",           HttpStatus.UNAUTHORIZED),
    USER_INACTIVE("auth.user.inactive",                       HttpStatus.FORBIDDEN),
    OTP_NOT_FOUND("auth.otp.not_found",                       HttpStatus.NOT_FOUND),
    OTP_INVALID("auth.otp.invalid",                           HttpStatus.BAD_REQUEST),
    OTP_EXPIRED("auth.otp.expired",                           HttpStatus.BAD_REQUEST),
    OTP_USED("auth.otp.used",                                 HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("auth.email.already_exists",         HttpStatus.CONFLICT),

    // ─── User ────────────────────────────────────────────────────────────────
    USER_NOT_FOUND("user.not_found",                          HttpStatus.NOT_FOUND),
    USER_EMAIL_DUPLICATE("user.email.duplicate",              HttpStatus.CONFLICT),

    // ─── Patient ─────────────────────────────────────────────────────────────
    PATIENT_NOT_FOUND("patient.not_found",                    HttpStatus.NOT_FOUND),
    PATIENT_PHONE_DUPLICATE("patient.phone.duplicate",        HttpStatus.CONFLICT),

    // ─── Doctor ──────────────────────────────────────────────────────────────
    DOCTOR_NOT_FOUND("doctor.not_found",                      HttpStatus.NOT_FOUND),
    DOCTOR_ALREADY_EXISTS("doctor.already_exists",            HttpStatus.CONFLICT),

    // ─── Appointment ─────────────────────────────────────────────────────────
    APPOINTMENT_NOT_FOUND("appointment.not_found",            HttpStatus.NOT_FOUND),
    APPOINTMENT_DOCTOR_BUSY("appointment.doctor.busy",        HttpStatus.CONFLICT),
    APPOINTMENT_TIME_REQUIRED("appointment.time_required",    HttpStatus.BAD_REQUEST),
    APPOINTMENT_CANCEL_FORBIDDEN("appointment.cancel_forbidden", HttpStatus.FORBIDDEN),
    APPOINTMENT_ALREADY_COMPLETED("appointment.already_completed", HttpStatus.CONFLICT),
    APPOINTMENT_PAST_TIME("appointment.past_time",            HttpStatus.BAD_REQUEST),
    APPOINTMENT_NOT_WORKING_DAY("appointment.doctor_not_working", HttpStatus.BAD_REQUEST),
    APPOINTMENT_STATUS_INVALID("appointment.status_invalid",  HttpStatus.BAD_REQUEST),
    APPOINTMENT_UPDATE_FORBIDDEN("appointment.update_forbidden", HttpStatus.FORBIDDEN),
    APPOINTMENT_CANCEL_TOO_LATE("appointment.cancel_too_late",   HttpStatus.BAD_REQUEST),
    APPOINTMENT_ALREADY_CONFIRMED("appointment.already_confirmed", HttpStatus.CONFLICT),
    APPOINTMENT_BOOKING_BLOCKED("appointment.booking_blocked",   HttpStatus.FORBIDDEN),
    APPOINTMENT_OUTSIDE_WORKING_HOURS("appointment.outside_working_hours", HttpStatus.BAD_REQUEST),
    APPOINTMENT_IN_PAST("appointment.in_past",                   HttpStatus.BAD_REQUEST),

    // ─── Payment ─────────────────────────────────────────────────────────────
    PAYMENT_NOT_FOUND("payment.not_found",                    HttpStatus.NOT_FOUND),
    PAYMENT_ALREADY_EXISTS("payment.already_exists",          HttpStatus.CONFLICT),
    PAYMENT_ALREADY_REFUNDED("payment.already_refunded",      HttpStatus.CONFLICT),
    PAYMENT_ONLY_PAID_REFUNDABLE("payment.only_paid_refundable", HttpStatus.BAD_REQUEST),

    // ─── Medical Service ─────────────────────────────────────────────────────
    SERVICE_NOT_FOUND("service.not_found",                    HttpStatus.NOT_FOUND),
    SERVICE_NAME_DUPLICATE("service.name_duplicate",          HttpStatus.CONFLICT),

    // ─── Medication ──────────────────────────────────────────────────────────
    MEDICATION_NOT_FOUND("medication.not_found",              HttpStatus.NOT_FOUND),

    // ─── Doctor Schedule ─────────────────────────────────────────────────────
    INVALID_SCHEDULE_TIME("doctor.schedule.invalid_time",     HttpStatus.BAD_REQUEST),
    DUPLICATE_SCHEDULE_DAY("doctor.schedule.duplicate_day",   HttpStatus.BAD_REQUEST),

    // ─── General ─────────────────────────────────────────────────────────────
    INTERNAL_SERVER_ERROR("general.server_error",             HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_DENIED("general.access_denied",                    HttpStatus.FORBIDDEN),
    VALIDATION_ERROR("general.error",                         HttpStatus.BAD_REQUEST);

    private final String messageKey;
    private final HttpStatus status;

    ErrorType(String messageKey, HttpStatus status) {
        this.messageKey = messageKey;
        this.status     = status;
    }
}