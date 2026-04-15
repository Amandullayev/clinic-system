package uz.clinic.service;

import uz.clinic.dto.request.ClinicSettingsRequest;
import uz.clinic.dto.response.ClinicSettingsResponse;

public interface ClinicSettingsService {
    ClinicSettingsResponse get();
    ClinicSettingsResponse update(ClinicSettingsRequest request);
}