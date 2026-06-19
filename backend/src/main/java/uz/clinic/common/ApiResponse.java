package uz.clinic.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import uz.clinic.enums.errors.SuccessType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    // ─── Mavjud metodlar (orqaga muvofiqligi saqlanadi) ──────────────────────

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // ─── SuccessType asosidagi yangi metodlar ─────────────────────────────────

    /**
     * SuccessType va data bilan — xabar messageKey dan olinadi (controller da resolve qilinadi).
     */
    public static <T> ApiResponse<T> of(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> of(String message) {
        return new ApiResponse<>(true, message, null);
    }

    // ─── ResponseEntity wrapper ───────────────────────────────────────────────

    /**
     * SuccessType dan HTTP status va xabarni birgalikda qaytaradi.
     * Controllerda: return ApiResponse.respond(SuccessType.PATIENT_CREATED, savedPatient, msg);
     */
    public static <T> ResponseEntity<ApiResponse<T>> respond(SuccessType type, T data, String message) {
        return ResponseEntity
                .status(type.getStatus())
                .body(new ApiResponse<>(true, message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> respond(SuccessType type, String message) {
        return ResponseEntity
                .status(type.getStatus())
                .body(new ApiResponse<>(true, message, null));
    }
}
