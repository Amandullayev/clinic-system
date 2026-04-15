package uz.clinic.dto.request;

import lombok.Data;

@Data
public class ClinicSettingsRequest {
    private String clinicName;
    private String phone;
    private String email;
    private String website;
    private String address;
    private String openTime;
    private String closeTime;
}