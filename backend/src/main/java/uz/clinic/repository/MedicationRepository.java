package uz.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.clinic.entity.Medication;

import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

    List<Medication> findAllByActiveTrue();

    @Query("SELECT m FROM Medication m WHERE m.active = true AND m.quantity < m.minQuantity")
    List<Medication> findLowStockMedications();
}