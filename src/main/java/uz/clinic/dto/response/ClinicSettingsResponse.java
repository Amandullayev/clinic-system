package uz.clinic.dto.response;

import lombok.Data;

@Data
public class ClinicSettingsResponse {
    private String clinicName;
    private String phone;
    private String email;
    private String website;
    private String address;
    private String openTime;
    private String closeTime;
}