package uz.clinic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.DiagnoseRequest;
import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.dto.response.PatientResponse;
import uz.clinic.service.DoctorPanelService;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorPanelController {

    private final DoctorPanelService doctorPanelService;

    @GetMapping("/my-appointments")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getMyAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(
                doctorPanelService.getMyAppointments(userDetails.getUsername())));
    }

    @GetMapping("/my-patients")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getMyPatients(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(
                doctorPanelService.getMyPatients(userDetails.getUsername())));
    }

    @PatchMapping("/appointments/{id}/status")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Status yangilandi",
                doctorPanelService.updateAppointmentStatus(id, status, userDetails.getUsername())));
    }

    @PatchMapping("/appointments/{id}/diagnose")
    public ResponseEntity<ApiResponse<AppointmentResponse>> writeDiagnosis(
            @PathVariable Long id,
            @RequestBody DiagnoseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Tashxis saqlandi",
                doctorPanelService.writeDiagnosis(id, request, userDetails.getUsername())));
    }
}