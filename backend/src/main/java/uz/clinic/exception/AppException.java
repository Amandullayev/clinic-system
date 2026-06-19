package uz.clinic.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import uz.clinic.enums.errors.ErrorType;

/**
 * Loyihadagi yagona custom exception.
 * BadRequestException va ResourceNotFoundException o'rniga shu ishlatiladi.
 *
 * Ishlatish:
 *   throw new AppException(ErrorType.PATIENT_NOT_FOUND);
 *   throw new AppException(ErrorType.APPOINTMENT_DOCTOR_BUSY);
 */
@Getter
public class AppException extends RuntimeException {

    private final ErrorType errorType;

    public AppException(ErrorType errorType) {
        super(errorType.getMessageKey());
        this.errorType = errorType;
    }

    public HttpStatus getStatus() {
        return errorType.getStatus();
    }

    public String getMessageKey() {
        return errorType.getMessageKey();
    }
}