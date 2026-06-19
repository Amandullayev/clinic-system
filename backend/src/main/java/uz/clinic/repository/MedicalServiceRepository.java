package uz.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.clinic.entity.MedicalService;
import uz.clinic.enums.ServiceCategory;

import java.util.List;

public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {

    List<MedicalService> findByCategoryAndActiveTrue(ServiceCategory category);

    // BUG #3 TUZATILDI: ServiceController category filtri uchun
    List<MedicalService> findAllByCategoryAndActiveTrue(ServiceCategory category);

    List<MedicalService> findAllByActiveTrue();

    List<MedicalService> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);
}