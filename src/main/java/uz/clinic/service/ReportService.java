package uz.clinic.service;

import uz.clinic.dto.response.ReportResponse;

import java.time.LocalDate;

public interface ReportService {
    ReportResponse getReport(LocalDate from, LocalDate to);
}