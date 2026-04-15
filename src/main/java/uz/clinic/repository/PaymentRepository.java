package uz.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.clinic.entity.Payment;
import uz.clinic.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByAppointmentId(Long appointmentId);

    List<Payment> findAllByStatus(PaymentStatus status);

    List<Payment> findByPaymentMethod(String paymentMethod);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'PAID' AND p.paidAt >= :start AND p.paidAt < :end")
    BigDecimal sumPaidAmountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Payment> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT p FROM Payment p WHERE p.status = 'PAID' AND p.paidAt >= :start AND p.paidAt <= :end")
    List<Payment> findPaidBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    boolean existsByAppointmentId(Long appointmentId);
}