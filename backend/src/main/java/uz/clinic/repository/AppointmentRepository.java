package uz.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.clinic.entity.Appointment;
import uz.clinic.entity.Patient;
import uz.clinic.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentTimeAndIdNot(Long doctorId, LocalDateTime appointmentTime, Long id);

    // YANGI: tasdiqlash va bekor qilish uchun token bo'yicha qidiruv
    Optional<Appointment> findByConfirmToken(String confirmToken);

    List<Appointment> findByPatient(Patient patient);
    List<Appointment> findAllByDoctorId(Long doctorId);
    List<Appointment> findAllByStatusNot(AppointmentStatus status);
    List<Appointment> findAllByPatientId(Long patientId);
    List<Appointment> findAllByStatus(AppointmentStatus status);
    List<Appointment> findAllByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentTime >= :start AND a.appointmentTime < :end ORDER BY a.appointmentTime ASC")
    List<Appointment> findTodayAppointments(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Appointment> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT a FROM Appointment a WHERE a.status = 'COMPLETED' AND a.appointmentTime >= :start AND a.appointmentTime <= :end")
    List<Appointment> findCompletedBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentTime >= :start AND a.appointmentTime <= :end")
    List<Appointment> findAllBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    boolean existsByDoctorIdAndAppointmentTime(Long doctorId, LocalDateTime appointmentTime);

    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor.id = :doctorId")
    List<Patient> findDistinctPatientsByDoctorId(@Param("doctorId") Long doctorId);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentTime >= :start AND a.appointmentTime < :end")
    long countTodayAppointments(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    long countByStatus(AppointmentStatus status);

    boolean existsByDoctorIdAndAppointmentTimeBetweenAndStatusNot(
            Long doctorId, LocalDateTime from, LocalDateTime to, AppointmentStatus status);

    // YANGI: update paytida o'zini chiqarib tekshirish
    boolean existsByDoctorIdAndAppointmentTimeBetweenAndStatusNotAndIdNot(
            Long doctorId, LocalDateTime from, LocalDateTime to, AppointmentStatus status, Long id);

    List<Appointment> findByDoctorIdAndAppointmentTimeBetweenAndStatusNot(
            Long doctorId, LocalDateTime start, LocalDateTime end, AppointmentStatus status);

    List<Appointment> findTop5ByPatientIdOrderByAppointmentTimeDesc(Long patientId);

    // YANGI: 24 soat o'tgan PENDING → AUTO_CANCELLED uchun
    @Query("SELECT a FROM Appointment a WHERE a.status = 'PENDING' AND a.createdAt < :deadline")
    List<Appointment> findUnconfirmedBefore(@Param("deadline") LocalDateTime deadline);

    // YANGI: vaqti o'tgan CONFIRMED/ARRIVED → NO_SHOW uchun
    @Query("SELECT a FROM Appointment a WHERE a.status IN ('CONFIRMED', 'ARRIVED') " +
            "AND a.appointmentTime < :threshold")
    List<Appointment> findExpiredConfirmed(@Param("threshold") LocalDateTime threshold);

    // YANGI: bugungi ARRIVED bemorlar — shifokor dashboardi uchun, kelish vaqti bo'yicha
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.status = 'ARRIVED' " +
            "AND a.appointmentTime >= :dayStart AND a.appointmentTime < :dayEnd " +
            "ORDER BY a.arrivedAt ASC")
    List<Appointment> findArrivedByDoctorToday(
            @Param("doctorId") Long doctorId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd);

    // YANGI: shu kun, shu shifokor uchun navbat raqami hisoblash
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.appointmentTime >= :dayStart AND a.appointmentTime < :dayEnd " +
            "AND a.status NOT IN ('CANCELLED', 'AUTO_CANCELLED')")
    long countActiveForDoctorOnDay(
            @Param("doctorId") Long doctorId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd);

    // YANGI: qabuldan 2 soat oldin eslatma yuborish uchun
    @Query("SELECT a FROM Appointment a WHERE a.status = 'CONFIRMED' " +
            "AND a.appointmentTime >= :from AND a.appointmentTime < :to")
    List<Appointment> findConfirmedBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}