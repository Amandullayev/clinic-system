package uz.clinic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.PaymentRequest;
import uz.clinic.dto.response.PaymentResponse;
import uz.clinic.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAll(
            @RequestParam(required = false) String method) {
        if (method != null && !method.isBlank()) {
            return ResponseEntity.ok(ApiResponse.ok(paymentService.getByMethod(method)));
        }
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PaymentResponse>> create(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("To'lov amalga oshirildi", paymentService.create(request)));
    }

    @PatchMapping("/{id}/refund")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("To'lov qaytarildi", paymentService.refund(id)));
    }
}