package uz.clinic.service;

import uz.clinic.dto.request.PaymentRequest;
import uz.clinic.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    List<PaymentResponse> getAll();
    PaymentResponse getById(Long id);
    PaymentResponse create(PaymentRequest request);
    PaymentResponse refund(Long id);
    List<PaymentResponse> getByMethod(String method);
}