package uz.clinic.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uz.clinic.common.ApiResponse;
import uz.clinic.enums.errors.ErrorType;
import uz.clinic.service.MessageService;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageService messageService;

    // ─── Asosiy handler ──────────────────────────────────────────────────────

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleApp(AppException ex) {
        String message = messageService.get(ex.getMessageKey());
        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.error(message));
    }

    // ─── @Valid annotatsiya xatolari ─────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(this::resolveFieldError)
                .findFirst()
                .orElse(messageService.get(ErrorType.VALIDATION_ERROR.getMessageKey()));

        return ResponseEntity
                .status(ErrorType.VALIDATION_ERROR.getStatus())
                .body(ApiResponse.error(message));
    }

    // ─── Eski exception lar (agar boshqa joyda ishlatilayotgan bo'lsa) ───────
    // Refactoring yakunlangach bu 2 ta handler o'chirilishi mumkin

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(404)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // ─── Kutilmagan xatolar ───────────────────────────────────────────────────

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Void>> handleSecurity(SecurityException ex) {
        return ResponseEntity
                .status(ErrorType.ACCESS_DENIED.getStatus())
                .body(ApiResponse.error(messageService.get(ErrorType.ACCESS_DENIED.getMessageKey())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
                .status(ErrorType.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.error(messageService.get(ErrorType.INTERNAL_SERVER_ERROR.getMessageKey())));
    }

    // ─── Yordamchi ───────────────────────────────────────────────────────────

    private String resolveFieldError(FieldError fieldError) {
        String msg = fieldError.getDefaultMessage();
        if (msg == null) return messageService.get(ErrorType.VALIDATION_ERROR.getMessageKey());
        try {
            return messageService.get(msg);
        } catch (Exception e) {
            return msg;
        }
    }
}