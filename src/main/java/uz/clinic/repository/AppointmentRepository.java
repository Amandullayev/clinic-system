package uz.clinic.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.clinic.entity.Appointment;
import uz.clinic.entity.User;
import uz.clinic.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentTimeAndIdNot(Long doctorId, LocalDateTime appointmentTime, Long id);

    List<Appointment> findByPatient(User patient);

    List<Appointment> findAllByDoctorId(Long doctorId);

    List<Appointment> findAllByPatientId(Long patientId);

    List<Appointment> findAllByStatus(AppointmentStatus status);

    List<Appointment> findAllByAppointmentTimeBetween(
            LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentTime >= :start AND a.appointmentTime < :end ORDER BY a.appointmentTime ASC")
    List<Appointment> findTodayAppointments(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Appointment> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT a FROM Appointment a WHERE a.status = 'COMPLETED' AND a.appointmentTime >= :start AND a.appointmentTime <= :end")
    List<Appointment> findCompletedBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentTime >= :start AND a.appointmentTime <= :end")
    List<Appointment> findAllBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    boolean existsByDoctorIdAndAppointmentTime(
            Long doctorId, LocalDateTime appointmentTime);
}