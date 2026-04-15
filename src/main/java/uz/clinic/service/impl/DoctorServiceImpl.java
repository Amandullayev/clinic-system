package uz.clinic.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.clinic.dto.request.DoctorRequest;
import uz.clinic.dto.response.DoctorResponse;
import uz.clinic.entity.Doctor;
import uz.clinic.entity.User;
import uz.clinic.enums.DoctorStatus;
import uz.clinic.exception.BadRequestException;
import uz.clinic.exception.ResourceNotFoundException;
import uz.clinic.mapper.DoctorMapper;
import uz.clinic.repository.DoctorRepository;
import uz.clinic.repository.UserRepository;
import uz.clinic.service.DoctorService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DoctorMapper doctorMapper;

    @Override
    public DoctorResponse getById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shifokor topilmadi: " + id));
        return doctorMapper.toResponse(doctor);
    }

    @Override
    public DoctorResponse create(DoctorRequest request) {
        if (doctorRepository.existsByUserId(request.getUserId())) {
            throw new BadRequestException("Bu foydalanuvchi allaqachon shifokor sifatida ro'yxatdan o'tgan");
        }
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi: " + request.getUserId()));

        Doctor doctor = Doctor.builder()
                .user(user)
                .specialization(request.getSpecialization())
                .phone(request.getPhone())
                .licenseNumber(request.getLicenseNumber())
                .experienceYears(request.getExperienceYears())
                .workingDays(request.getWorkingDays())
                .workStartTime(request.getWorkStartTime())
                .workEndTime(request.getWorkEndTime())
                .status(request.getStatus() != null ? request.getStatus() : DoctorStatus.ACTIVE)
                .active(true)
                .build();

        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Override
    public DoctorResponse update(Long id, DoctorRequest request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shifokor topilmadi: " + id));
        doctor.setSpecialization(request.getSpecialization());
        doctor.setPhone(request.getPhone());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setWorkingDays(request.getWorkingDays());
        doctor.setWorkStartTime(request.getWorkStartTime());
        doctor.setWorkEndTime(request.getWorkEndTime());
        if (request.getStatus() != null) {
            doctor.setStatus(request.getStatus());
        }
        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Override
    public void delete(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shifokor topilmadi: " + id));
        doctor.setActive(false);
        doctorRepository.save(doctor);
    }

    @Override
    public List<DoctorResponse> getAll() {
        return doctorRepository.findAll()
                .stream()
                .filter(Doctor::isActive)
                .map(doctorMapper::toResponse)
                .toList();
    }
}