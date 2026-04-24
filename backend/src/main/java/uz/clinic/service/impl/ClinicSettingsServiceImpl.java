package uz.clinic.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.dto.request.ClinicSettingsRequest;
import uz.clinic.dto.response.ClinicSettingsResponse;
import uz.clinic.entity.ClinicSettings;
import uz.clinic.repository.ClinicSettingsRepository;
import uz.clinic.service.ClinicSettingsService;

@Service
@RequiredArgsConstructor
public class ClinicSettingsServiceImpl implements ClinicSettingsService {

    private final ClinicSettingsRepository clinicSettingsRepository;

    @Override
    public ClinicSettingsResponse get() {
        ClinicSettings settings = clinicSettingsRepository.findById(1L)
                .orElseGet(() -> {
                    // Agar hali yozilmagan bo'lsa, default qaytaradi
                    ClinicSettings defaults = new ClinicSettings();
                    defaults.setId(1L);
                    defaults.setClinicName("CLINIC Medical Center");
                    defaults.setPhone("+998 71 123 45 67");
                    defaults.setEmail("info@cliniq.uz");
                    defaults.setWebsite("www.cliniq.uz");
                    defaults.setAddress("Toshkent shahar, Chilonzor tumani");
                    defaults.setOpenTime("08:00");
                    defaults.setCloseTime("20:00");
                    return clinicSettingsRepository.save(defaults);
                });
        return toResponse(settings);
    }

    @Override
    public ClinicSettingsResponse update(ClinicSettingsRequest request) {
        ClinicSettings settings = clinicSettingsRepository.findById(1L)
                .orElse(new ClinicSettings());
        settings.setId(1L);
        settings.setClinicName(request.getClinicName());
        settings.setPhone(request.getPhone());
        settings.setEmail(request.getEmail());
        settings.setWebsite(request.getWebsite());
        settings.setAddress(request.getAddress());
        settings.setOpenTime(request.getOpenTime());
        settings.setCloseTime(request.getCloseTime());
        return toResponse(clinicSettingsRepository.save(settings));
    }

    private ClinicSettingsResponse toResponse(ClinicSettings settings) {
        ClinicSettingsResponse response = new ClinicSettingsResponse();
        response.setClinicName(settings.getClinicName());
        response.setPhone(settings.getPhone());
        response.setEmail(settings.getEmail());
        response.setWebsite(settings.getWebsite());
        response.setAddress(settings.getAddress());
        response.setOpenTime(settings.getOpenTime());
        response.setCloseTime(settings.getCloseTime());
        return response;
    }
}