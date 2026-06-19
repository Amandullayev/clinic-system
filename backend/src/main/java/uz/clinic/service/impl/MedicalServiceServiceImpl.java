package uz.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.dto.request.ServiceRequest;
import uz.clinic.dto.response.ServiceResponse;
import uz.clinic.entity.MedicalService;
import uz.clinic.enums.errors.ErrorType;
import uz.clinic.enums.ServiceCategory;
import uz.clinic.exception.AppException;
import uz.clinic.mapper.ServiceMapper;
import uz.clinic.repository.MedicalServiceRepository;
import uz.clinic.service.MedicalServiceService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalServiceServiceImpl implements MedicalServiceService {

    private final MedicalServiceRepository medicalServiceRepository;
    private final ServiceMapper serviceMapper;

    // BUG #3 TUZATILDI: category bo'yicha filter ishlaydi
    @Override
    public List<ServiceResponse> getAll(ServiceCategory category) {
        List<MedicalService> services = (category != null)
                ? medicalServiceRepository.findAllByCategoryAndActiveTrue(category)
                : medicalServiceRepository.findAllByActiveTrue();
        return services.stream()
                .map(serviceMapper::toResponse)
                .toList();
    }

    @Override
    public ServiceResponse getById(Long id) {
        return serviceMapper.toResponse(findById(id));
    }

    @Override
    public ServiceResponse create(ServiceRequest request) {
        if (medicalServiceRepository.existsByName(request.getName()))
            throw new AppException(ErrorType.SERVICE_NAME_DUPLICATE);
        return serviceMapper.toResponse(medicalServiceRepository.save(serviceMapper.toEntity(request)));
    }

    @Override
    public ServiceResponse update(Long id, ServiceRequest request) {
        MedicalService service = findById(id);
        serviceMapper.updateEntity(service, request);
        return serviceMapper.toResponse(medicalServiceRepository.save(service));
    }

    @Override
    public void delete(Long id) {
        MedicalService service = findById(id);
        service.setActive(false);
        medicalServiceRepository.save(service);
    }

    private MedicalService findById(Long id) {
        return medicalServiceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorType.SERVICE_NOT_FOUND));
    }
}
