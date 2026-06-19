package uz.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.clinic.entity.Doctor;
import uz.clinic.entity.DoctorSchedule;
import uz.clinic.enums.AppointmentStatus;
import uz.clinic.enums.errors.ErrorType;
import uz.clinic.exception.AppException;
import uz.clinic.repository.AppointmentRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;

// YANGI: Appointment yaratish/yangilashda ishlatiladigan umumiy qoidalar.
// Avval AppointmentServiceImpl (Admin/Receptionist) va PatientPanelServiceImpl (Patient)
// bir xil qabulni ikki xil qoida bilan tekshirardi:
//   - Admin/Receptionist: faqat aynan bir xil vaqt band emasligini tekshirardi,
//     ish kuni/ish vaqti va o'tgan vaqt umuman tekshirilmasdi.
//   - Patient: DoctorSchedule asosida kun+vaqt tekshirilardi, lekin 30 daqiqalik
//     interval bilan band tekshiruvi va o'tgan vaqt xabari noto'g'ri edi.
// Endi ikkala oqim ham shu klassdagi bitta metodni chaqiradi —
// shu bilan qoidalar bir joyda, bir xil bo'ladi.
@Component
@RequiredArgsConstructor
public class AppointmentValidator {

    private final AppointmentRepository appointmentRepository;

    // Band tekshiruvida ishlatiladigan oraliq (daqiqalarda).
    // Masalan 16:00 ga yozilgan bo'lsa, 15:31-16:29 oralig'ida boshqa qabul bo'lishi mumkin emas.
    private static final int BUSY_WINDOW_MINUTES = 29;

    /**
     * Yangi appointment yaratishda yoki vaqtini o'zgartirishda chaqiriladi.
     *
     * @param doctor          shifokor (schedules to'liq yuklangan bo'lishi kerak)
     * @param appointmentTime tekshirilayotgan sana+vaqt
     * @param excludeId       update paytida o'zining yozuvini band tekshiruvidan chiqarish uchun
     *                        (create paytida null beriladi)
     */
    public void validate(Doctor doctor, LocalDateTime appointmentTime, Long excludeId) {
        if (appointmentTime == null)
            throw new AppException(ErrorType.APPOINTMENT_TIME_REQUIRED);

        // 1. O'tgan vaqtga yozish mumkin emas
        if (appointmentTime.isBefore(LocalDateTime.now()))
            throw new AppException(ErrorType.APPOINTMENT_IN_PAST);

        // 2. Shifokor shu kuni ishlaydimi
        DoctorSchedule schedule = doctor.getScheduleFor(appointmentTime.getDayOfWeek());
        if (schedule == null)
            throw new AppException(ErrorType.APPOINTMENT_NOT_WORKING_DAY);

        // 3. Vaqt ish vaqti oralig'ida ekanligini tekshirish
        LocalTime apptLocalTime = appointmentTime.toLocalTime();
        if (apptLocalTime.isBefore(schedule.getStartTime()) || !apptLocalTime.isBefore(schedule.getEndTime()))
            throw new AppException(ErrorType.APPOINTMENT_OUTSIDE_WORKING_HOURS);

        // 4. Band vaqtni tekshirish — 29 daqiqalik oraliqda boshqa faol qabul bo'lmasligi kerak.
        // excludeId berilgan bo'lsa (update), o'zining yozuvi hisobga olinmaydi.
        LocalDateTime from = appointmentTime.minusMinutes(BUSY_WINDOW_MINUTES);
        LocalDateTime to   = appointmentTime.plusMinutes(BUSY_WINDOW_MINUTES);

        boolean busy = (excludeId == null)
                ? appointmentRepository.existsByDoctorIdAndAppointmentTimeBetweenAndStatusNot(
                doctor.getId(), from, to, AppointmentStatus.CANCELLED)
                : appointmentRepository.existsByDoctorIdAndAppointmentTimeBetweenAndStatusNotAndIdNot(
                doctor.getId(), from, to, AppointmentStatus.CANCELLED, excludeId);

        if (busy)
            throw new AppException(ErrorType.APPOINTMENT_DOCTOR_BUSY);
    }

    /**
     * Faqat bo'sh slotlar ro'yxati uchun ishlatiladi (PatientPanelServiceImpl.getAvailableSlots).
     * O'tgan vaqt va band tekshiruvi shu yerda emas, chaqiruvchi tomonda alohida bo'ladi —
     * chunki u butun kun bo'yicha slotlar generatsiya qilinadi.
     */
    public DoctorSchedule getScheduleOrNull(Doctor doctor, java.time.DayOfWeek dayOfWeek) {
        return doctor.getScheduleFor(dayOfWeek);
    }
}