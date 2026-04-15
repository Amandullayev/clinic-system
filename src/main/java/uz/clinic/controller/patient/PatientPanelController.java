package uz.clinic.controller.patient;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.AppointmentRequest;
import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.exception.ResourceNotFoundException;
import uz.clinic.service.patient.PatientPanelService;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
public class PatientPanelController {

    private final PatientPanelService patientPanelService;

    @GetMapping("/my-appointments")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getMyAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(
                patientPanelService.getMyAppointments(userDetails.getUsername())));
    }

    @PostMapping("/appointments")
    public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(
            @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Uchrashuv band qilindi",
                patientPanelService.bookAppointment(request, userDetails.getUsername())));
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        patientPanelService.cancelAppointment(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Uchrashuv bekor qilindi", null));
    }
}