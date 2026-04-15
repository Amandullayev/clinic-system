package uz.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.dto.request.ServiceRequest;
import uz.clinic.dto.response.ServiceResponse;
import uz.clinic.entity.MedicalService;
import uz.clinic.exception.BadRequestException;
import uz.clinic.exception.ResourceNotFoundException;
import uz.clinic.mapper.ServiceMapper;
import uz.clinic.repository.MedicalServiceRepository;
import uz.clinic.service.MedicalServiceService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalServiceServiceImpl implements MedicalServiceService {

    private final MedicalServiceRepository medicalServiceRepository;
    private final ServiceMapper serviceMapper;

    @Override
    public ServiceResponse getById(Long id) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Xizmat topilmadi: " + id));
        return serviceMapper.toResponse(service);
    }

    @Override
    public ServiceResponse create(ServiceRequest request) {
        if (medicalServiceRepository.existsByName(request.getName())) {
            throw new BadRequestException("Bu nomli xizmat allaqachon mavjud");
        }
        MedicalService service = serviceMapper.toEntity(request);
        return serviceMapper.toResponse(medicalServiceRepository.save(service));
    }

    @Override
    public ServiceResponse update(Long id, ServiceRequest request) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Xizmat topilmadi: " + id));
        serviceMapper.updateEntity(service, request);
        return serviceMapper.toResponse(medicalServiceRepository.save(service));
    }

    @Override
    public void delete(Long id) {
        MedicalService service = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Xizmat topilmadi: " + id));
        service.setActive(false);
        medicalServiceRepository.save(service);
    }

    @Override
    public List<ServiceResponse> getAll() {
        return medicalServiceRepository.findAll()
                .stream()
                .filter(MedicalService::isActive)
                .map(serviceMapper::toResponse)
                .toList();
    }
}