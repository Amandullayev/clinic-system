package uz.clinic.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.dto.response.*;
import uz.clinic.entity.Appointment;
import uz.clinic.entity.Payment;
import uz.clinic.repository.AppointmentRepository;
import uz.clinic.repository.DoctorRepository;
import uz.clinic.repository.PatientRepository;
import uz.clinic.repository.PaymentRepository;
import uz.clinic.service.ReportService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public ReportResponse getReport(LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);

        List<Appointment> appointments = appointmentRepository.findAllBetween(start, end);
        List<Appointment> completed = appointmentRepository.findCompletedBetween(start, end);
        List<Payment> paidPayments = paymentRepository.findPaidBetween(start, end);

        // Umumiy statistika
        ReportResponse response = new ReportResponse();
        response.setTotalPatients(patientRepository.count());
        response.setTotalVisits(completed.size());

        BigDecimal totalRevenue = paidPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalRevenue(totalRevenue);

        if (!paidPayments.isEmpty()) {
            response.setAvgPrice(totalRevenue.divide(
                    BigDecimal.valueOf(paidPayments.size()), 0, RoundingMode.HALF_UP));
        } else {
            response.setAvgPrice(BigDecimal.ZERO);
        }

        // Eng mashhur xizmatlar
        Map<String, Long> serviceCount = appointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getMedicalService().getName(),
                        Collectors.counting()
                ));
        List<ServiceStatDto> popularServices = serviceCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> new ServiceStatDto(e.getKey(), e.getValue()))
                .toList();
        response.setPopularServices(popularServices);

        // Oylik daromad
        Map<String, BigDecimal> monthlyMap = new LinkedHashMap<>();
        paidPayments.forEach(p -> {
            String month = p.getPaidAt().getMonth()
                    .getDisplayName(TextStyle.SHORT, new Locale("uz"));
            monthlyMap.merge(month, p.getAmount(), BigDecimal::add);
        });
        response.setMonthlyRevenue(monthlyMap.entrySet().stream()
                .map(e -> new MonthlyRevenueDto(e.getKey(), e.getValue()))
                .toList());

        // Hafta kunlari bo'yicha tashriflar
        String[] days = {"Dushanba", "Seshanba", "Chorshanba", "Payshanba", "Juma", "Shanba", "Yakshanba"};
        DayOfWeek[] dayOfWeeks = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};

        List<WeekdayVisitDto> weekdayVisits = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            DayOfWeek dow = dayOfWeeks[i];
            long count = appointments.stream()
                    .filter(a -> a.getAppointmentTime().getDayOfWeek() == dow)
                    .count();
            weekdayVisits.add(new WeekdayVisitDto(days[i], count));
        }
        response.setWeekdayVisits(weekdayVisits);

        // Shifokorlar samaradorligi
        List<DoctorEfficiencyDto> doctorEfficiency = doctorRepository.findAllByActiveTrue()
                .stream()
                .map(doctor -> {
                    List<Appointment> docApps = completed.stream()
                            .filter(a -> a.getDoctor().getId().equals(doctor.getId()))
                            .toList();

                    BigDecimal docRevenue = paidPayments.stream()
                            .filter(p -> p.getAppointment().getDoctor().getId().equals(doctor.getId()))
                            .map(Payment::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal docAvg = docApps.isEmpty() ? BigDecimal.ZERO :
                            docRevenue.divide(BigDecimal.valueOf(docApps.size()), 0, RoundingMode.HALF_UP);

                    // Ish kunlari: berilgan oraliqda dushanba-juma kunlari soni
                    long workDays = from.datesUntil(to.plusDays(1))
                            .filter(d -> d.getDayOfWeek().getValue() <= 5)
                            .count();

                    return new DoctorEfficiencyDto(
                            doctor.getUser().getFullName(),
                            docApps.size(),
                            (int) workDays,
                            docRevenue,
                            docAvg
                    );
                })
                .toList();
        response.setDoctorEfficiency(doctorEfficiency);

        return response;
    }
}