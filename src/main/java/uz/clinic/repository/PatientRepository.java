package uz.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.clinic.entity.Patient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPhone(String phone);

    boolean existsByPhone(String phone);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.createdAt >= :start AND p.createdAt < :end")
    long countNewPatients(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    Optional<Patient> findByEmail(String email);

    List<Patient> findByFullNameContainingIgnoreCase(String fullName);

}