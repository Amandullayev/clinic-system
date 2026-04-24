package uz.clinic.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.dto.response.DashboardResponse;
import uz.clinic.dto.response.WeekdayVisitDto;
import uz.clinic.enums.AppointmentStatus;
import uz.clinic.mapper.AppointmentMapper;
import uz.clinic.mapper.DoctorMapper;
import uz.clinic.mapper.MedicationMapper;
import uz.clinic.mapper.PaymentMapper;
import uz.clinic.repository.*;
import uz.clinic.service.DashboardService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MedicationRepository medicationRepository;

    private final AppointmentMapper appointmentMapper;
    private final PaymentMapper paymentMapper;
    private final DoctorMapper doctorMapper;
    private final MedicationMapper medicationMapper;

    @Override
    public DashboardResponse getDashboard() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        YearMonth currentMonth = YearMonth.now();
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        DashboardResponse response = new DashboardResponse();

        // Statistika
        response.setTodayPatients(
                (int) appointmentRepository.countTodayAppointments(todayStart, todayEnd)
        );
        response.setPendingAppointments(
                (int) appointmentRepository.countByStatus(AppointmentStatus.PENDING)
        );

        response.setNewPatientsToday(
                patientRepository.countNewPatients(todayStart, todayEnd)
        );

        BigDecimal revenue = paymentRepository.sumPaidAmountBetween(monthStart, monthEnd);
        response.setMonthlyRevenue(revenue != null ? revenue : BigDecimal.ZERO);

        // Bugungi navbatlar
        response.setTodayAppointments(
                appointmentRepository.findTodayAppointments(todayStart, todayEnd)
                        .stream()
                        .map(appointmentMapper::toResponse)
                        .toList()
        );

        // So'nggi 5 ta to'lov
        response.setRecentPayments(
                paymentRepository.findTop5ByOrderByCreatedAtDesc()
                        .stream()
                        .map(paymentMapper::toResponse)
                        .toList()
        );

        // Kam qolgan dorilar
        response.setLowStockMedications(
                medicationRepository.findLowStockMedications()
                        .stream()
                        .map(medicationMapper::toResponse)
                        .toList()
        );

        // Shifokorlar holati
        response.setDoctors(
                doctorRepository.findAllByActiveTrue()
                        .stream()
                        .map(doctorMapper::toResponse)
                        .toList()
        );

        // Haftalik statistika
        LocalDateTime weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
        List<WeekdayVisitDto> weekly = new java.util.ArrayList<>();
        String[] days = {"Dush", "Sesh", "Chor", "Pay", "Juma", "Shan", "Yak"};
        for (int i = 0; i < 7; i++) {
            LocalDateTime start = weekStart.plusDays(i);
            LocalDateTime end = start.plusDays(1);
            long count = appointmentRepository.countTodayAppointments(start, end);
            weekly.add(new WeekdayVisitDto(days[i], count));
        }
        response.setWeeklyVisits(weekly);

        return response;
    }
}