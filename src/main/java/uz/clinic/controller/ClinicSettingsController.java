package uz.clinic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.ClinicSettingsRequest;
import uz.clinic.dto.response.ClinicSettingsResponse;
import uz.clinic.service.ClinicSettingsService;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class ClinicSettingsController {

    private final ClinicSettingsService clinicSettingsService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<ClinicSettingsResponse>> get() {
        return ResponseEntity.ok(ApiResponse.ok(clinicSettingsService.get()));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping
    public ResponseEntity<ApiResponse<ClinicSettingsResponse>> update(
            @RequestBody ClinicSettingsRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Sozlamalar saqlandi", clinicSettingsService.update(request)));
    }
}