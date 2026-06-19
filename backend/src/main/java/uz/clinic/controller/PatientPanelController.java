package uz.clinic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.AppointmentRequest;
import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.service.PatientPanelService;

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
                "Navbat olindi. Emailingizni tekshiring — tasdiqlash havolasi yuborildi.",
                patientPanelService.bookAppointment(request, userDetails.getUsername())));
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        patientPanelService.cancelAppointment(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Navbat bekor qilindi", null));
    }

    @GetMapping("/doctors/{doctorId}/available-slots")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam String date) {
        return ResponseEntity.ok(ApiResponse.ok(
                patientPanelService.getAvailableSlots(doctorId, date)));
    }

    // YANGI: email havolasi orqali tasdiqlash (token query param)
    // Frontend: /confirm-appointment?token=xxx → bu endpointga keladi
    @PostMapping("/appointments/confirm")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<AppointmentResponse>> confirmByToken(
            @RequestParam String token) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Navbat tasdiqlandi!",
                patientPanelService.confirmByToken(token)));
    }

    // YANGI: email havolasi orqali bekor qilish
    // Frontend: /cancel-appointment?token=xxx → bu endpointga keladi
    @PostMapping("/appointments/cancel-by-token")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<Void>> cancelByToken(
            @RequestParam String token) {
        patientPanelService.cancelByToken(token);
        return ResponseEntity.ok(ApiResponse.ok("Navbat bekor qilindi", null));
    }
}