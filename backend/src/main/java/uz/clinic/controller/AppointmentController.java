package uz.clinic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.clinic.common.ApiResponse;
import uz.clinic.dto.request.AppointmentRequest;
import uz.clinic.dto.response.AppointmentResponse;
import uz.clinic.enums.AppointmentStatus;
import uz.clinic.service.AppointmentService;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/patient/my-appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getMyAppointments(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.ok(appointmentService.getAppointmentsByPatientEmail(email)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(appointmentService.getAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(appointmentService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> create(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Qabul yaratildi", appointmentService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> update(@PathVariable Long id,
                                                                   @Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Qabul yangilandi", appointmentService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Holat yangilandi", appointmentService.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Qabul o'chirildi", null));
    }

    // YANGI: Receptionist — bemor klinikaga kelganini belgilash
// Bosilgandan keyin bemor shifokor dashboardidagi navbat ro'yxatiga tushadi
    @PatchMapping("/{id}/arrived")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> markArrived(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Bemor keldi deb belgilandi",
                appointmentService.markArrived(id)));
    }

    // YANGI: Shifokor dashboardi — bugun kelgan, navbat kutayotgan bemorlar
// arrivedAt vaqti bo'yicha tartiblangan (kim oldin keldi, o'sha birinchi)
    @GetMapping("/today-queue")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getTodayQueue(
            @RequestParam Long doctorId) {
        return ResponseEntity.ok(ApiResponse.ok(
                appointmentService.getTodayQueue(doctorId)));
    }
}