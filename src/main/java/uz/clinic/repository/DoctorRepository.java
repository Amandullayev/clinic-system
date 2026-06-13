package uz.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.clinic.entity.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findAllByActiveTrue();

    List<Doctor> findAllBySpecialization(String specialization);

    Optional<Doctor> findByUserId(Long userId);

    Optional<Doctor> findByUser_Email(String email);

    List<Doctor> findByUser_Id(Long userId);

    boolean existsByUserId(Long userId);
}