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
import uz.clinic.service.MessageService;
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
    private final MessageService messageService;

    // Hafta kunlari message key lari — tarjima MessageService orqali olinadi
    private static final String[] DAY_KEYS = {
            "weekday.monday",
            "weekday.tuesday",
            "weekday.wednesday",
            "weekday.thursday",
            "weekday.friday",
            "weekday.saturday",
            "weekday.sunday"
    };

    private static final DayOfWeek[] DAY_OF_WEEKS = {
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
    };

    @Override
    public ReportResponse getReport(LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end   = to.atTime(23, 59, 59);

        List<Appointment> appointments  = appointmentRepository.findAllBetween(start, end);
        List<Appointment> completed     = appointmentRepository.findCompletedBetween(start, end);
        List<Payment>     paidPayments  = paymentRepository.findPaidBetween(start, end);

        ReportResponse response = new ReportResponse();

        // ─── Umumiy statistika ────────────────────────────────────────────────
        response.setTotalPatients(patientRepository.count());
        response.setTotalVisits(completed.size());

        BigDecimal totalRevenue = paidPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalRevenue(totalRevenue);

        response.setAvgPrice(paidPayments.isEmpty()
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(paidPayments.size()), 0, RoundingMode.HALF_UP));

        // ─── Eng mashhur xizmatlar ────────────────────────────────────────────
        Map<String, Long> serviceCount = appointments.stream()
                .filter(a -> a.getMedicalService() != null)          // null himoya
                .collect(Collectors.groupingBy(
                        a -> a.getMedicalService().getName(),
                        Collectors.counting()
                ));

        List<ServiceStatDto> popularServices = serviceCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> new ServiceStatDto(e.getKey(), e.getValue()))
                .toList();
        response.setPopularServices(popularServices);

        // ─── Oylik daromad ────────────────────────────────────────────────────
        // TUZATILDI: Locale("uz") o'rniga getCurrentLocale() — tanlangan tilga qarab
        Locale currentLocale = messageService.getCurrentLocale();

        Map<String, BigDecimal> monthlyMap = new LinkedHashMap<>();
        paidPayments.forEach(p -> {
            String month = p.getPaidAt().getMonth()
                    .getDisplayName(TextStyle.SHORT, currentLocale);
            monthlyMap.merge(month, p.getAmount(), BigDecimal::add);
        });

        response.setMonthlyRevenue(monthlyMap.entrySet().stream()
                .map(e -> new MonthlyRevenueDto(e.getKey(), e.getValue()))
                .toList());

        // ─── Hafta kunlari bo'yicha tashriflar ───────────────────────────────
        // TUZATILDI: hardcode o'zbek nomlari o'rniga MessageService orqali tarjima
        List<WeekdayVisitDto> weekdayVisits = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            DayOfWeek dow   = DAY_OF_WEEKS[i];
            String dayLabel = messageService.get(DAY_KEYS[i]);
            long count = appointments.stream()
                    .filter(a -> a.getAppointmentTime().getDayOfWeek() == dow)
                    .count();
            weekdayVisits.add(new WeekdayVisitDto(dayLabel, count));
        }
        response.setWeekdayVisits(weekdayVisits);

        // ─── Shifokorlar samaradorligi ────────────────────────────────────────
        long workDays = from.datesUntil(to.plusDays(1))
                .filter(d -> d.getDayOfWeek().getValue() <= 5)
                .count();

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

                    BigDecimal docAvg = docApps.isEmpty()
                            ? BigDecimal.ZERO
                            : docRevenue.divide(BigDecimal.valueOf(docApps.size()), 0, RoundingMode.HALF_UP);

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