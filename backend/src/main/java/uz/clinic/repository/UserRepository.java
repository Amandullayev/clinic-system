package uz.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.clinic.entity.User;
import uz.clinic.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRole(Role role);

    // BUG #1 TUZATILDI: ReceptionistService uchun — deleted=false filtri bilan
    List<User> findByRoleAndDeletedFalse(Role role);

    List<User> findAllByActiveTrue();

    List<User> findAllByDeletedFalse();

    // BUG #6 TUZATILDI: ID bo'yicha qidirish — deleted foydalanuvchi qaytmasin
    Optional<User> findByIdAndDeletedFalse(Long id);
}