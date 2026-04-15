package uz.clinic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.ServiceRequest;
import uz.clinic.dto.response.ServiceResponse;
import uz.clinic.enums.ServiceCategory;
import uz.clinic.service.MedicalServiceService;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final MedicalServiceService medicalServiceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'RECEPTIONIST', 'DOCTOR', 'PATIENT')")    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getAll(@RequestParam(required = false) ServiceCategory category) {
        return ResponseEntity.ok(ApiResponse.ok(medicalServiceService.getAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public ResponseEntity<ApiResponse<ServiceResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(medicalServiceService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<ServiceResponse>> create(@Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Xizmat qo'shildi", medicalServiceService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<ServiceResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Xizmat yangilandi", medicalServiceService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        medicalServiceService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Xizmat o'chirildi", null));
    }
}