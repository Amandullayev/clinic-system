package uz.clinic.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import uz.clinic.entity.ClinicSettings;

public interface ClinicSettingsRepository extends JpaRepository<ClinicSettings, Long> {
}