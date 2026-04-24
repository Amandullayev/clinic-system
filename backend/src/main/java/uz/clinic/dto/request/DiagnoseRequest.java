package uz.clinic.dto.request;

import lombok.Data;

@Data
public class DiagnoseRequest {
    private String diagnosis;
    private String prescription;
    private String notes;
}