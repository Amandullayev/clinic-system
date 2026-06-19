package uz.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.dto.request.PaymentRequest;
import uz.clinic.dto.response.PaymentResponse;
import uz.clinic.entity.Appointment;
import uz.clinic.entity.Payment;
import uz.clinic.enums.errors.ErrorType;
import uz.clinic.enums.PaymentStatus;
import uz.clinic.exception.AppException;
import uz.clinic.mapper.PaymentMapper;
import uz.clinic.repository.AppointmentRepository;
import uz.clinic.repository.PaymentRepository;
import uz.clinic.service.PaymentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public List<PaymentResponse> getAll() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse getById(Long id) {
        return paymentMapper.toResponse(findById(id));
    }

    @Override
    public PaymentResponse create(PaymentRequest request) {
        if (paymentRepository.existsByAppointmentId(request.getAppointmentId()))
            throw new AppException(ErrorType.PAYMENT_ALREADY_EXISTS);

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new AppException(ErrorType.APPOINTMENT_NOT_FOUND));

        Payment payment = Payment.builder()
                .appointment(appointment)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(request.getStatus() != null ? request.getStatus() : PaymentStatus.PENDING)
                .paidAt(request.getStatus() == PaymentStatus.PAID ? LocalDateTime.now() : null)
                .build();

        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Override
    public List<PaymentResponse> getByMethod(String method) {
        return paymentRepository.findByPaymentMethod(method)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse refund(Long id) {
        Payment payment = findById(id);

        if (payment.getStatus() == PaymentStatus.REFUNDED)
            throw new AppException(ErrorType.PAYMENT_ALREADY_REFUNDED);

        if (payment.getStatus() != PaymentStatus.PAID)
            throw new AppException(ErrorType.PAYMENT_ONLY_PAID_REFUNDABLE);

        payment.setStatus(PaymentStatus.REFUNDED);
        return paymentMapper.toResponse(paymentRepository.save(payment));
    }

    private Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorType.PAYMENT_NOT_FOUND));
    }
}