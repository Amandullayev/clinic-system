package uz.clinic.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.dto.request.PaymentRequest;
import uz.clinic.dto.response.PaymentResponse;
import uz.clinic.entity.Appointment;
import uz.clinic.entity.Payment;
import uz.clinic.enums.PaymentStatus;
import uz.clinic.exception.BadRequestException;
import uz.clinic.exception.ResourceNotFoundException;
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
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("To'lov topilmadi: " + id));
        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse create(PaymentRequest request) {
        if (paymentRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new BadRequestException("Bu qabul uchun to'lov allaqachon mavjud");
        }

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Qabul topilmadi"));

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
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("To'lov topilmadi: " + id));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new BadRequestException("Bu to'lov allaqachon qaytarilgan");
        }

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new BadRequestException("Faqat to'langan to'lovlarni qaytarish mumkin");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        return paymentMapper.toResponse(paymentRepository.save(payment));
    }
}