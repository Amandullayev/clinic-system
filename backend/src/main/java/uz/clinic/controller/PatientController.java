package uz.clinic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.PatientRequest;
import uz.clinic.dto.response.PatientResponse;
import uz.clinic.service.PatientService;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(patientService.getAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public ResponseEntity<ApiResponse<PatientResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(patientService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponse>> create(@Valid @RequestBody PatientRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Bemor qo'shildi", patientService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody PatientRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Bemor yangilandi", patientService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Bemor o'chirildi", null));
    }
}