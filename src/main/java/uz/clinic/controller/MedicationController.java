package uz.clinic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.MedicationRequest;
import uz.clinic.dto.response.MedicationResponse;
import uz.clinic.service.MedicationService;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<MedicationResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(medicationService.getAll()));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<MedicationResponse>>> getLowStock() {
        return ResponseEntity.ok(ApiResponse.ok(medicationService.getLowStock()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<MedicationResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(medicationService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<MedicationResponse>> create(@Valid @RequestBody MedicationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Dori qo'shildi", medicationService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<MedicationResponse>> update(@PathVariable Long id,
                                                                  @Valid @RequestBody MedicationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Dori yangilandi", medicationService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        medicationService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Dori o'chirildi", null));
    }
}