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

    List<User> findByRole(Role role);

    List<User> findAllByActiveTrue();
}