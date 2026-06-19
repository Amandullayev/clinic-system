package uz.clinic.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.clinic.enums.DoctorStatus;

import java.util.ArrayList;
import java.util.List;

@Data
public class DoctorRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String specialization;

    private String phone;
    private String licenseNumber;
    private Integer experienceYears;
    private DoctorStatus status;

    // O'ZGARTIRILDI: eski workingDays / workStartTime / workEndTime o'rniga
    // har bir hafta kuni uchun alohida jadval ro'yxati.
    // Bo'sh ro'yxat = shifokorning hech qaysi kuni belgilanmagan (hammasi dam olish).
    @Valid
    private List<DoctorScheduleRequest> schedules = new ArrayList<>();
}