package uz.clinic.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.clinic.dto.request.DoctorRequest;
import uz.clinic.dto.request.DoctorScheduleRequest;
import uz.clinic.dto.response.DoctorResponse;
import uz.clinic.entity.Doctor;
import uz.clinic.entity.DoctorSchedule;
import uz.clinic.entity.User;
import uz.clinic.enums.DoctorStatus;
import uz.clinic.enums.errors.ErrorType;
import uz.clinic.exception.AppException;
import uz.clinic.mapper.DoctorMapper;
import uz.clinic.repository.DoctorRepository;
import uz.clinic.repository.UserRepository;
import uz.clinic.service.DoctorService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DoctorMapper doctorMapper;

    @Override
    public DoctorResponse getById(Long id) {
        return doctorMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public DoctorResponse create(DoctorRequest request) {
        if (doctorRepository.existsByUserId(request.getUserId()))
            throw new AppException(ErrorType.DOCTOR_ALREADY_EXISTS);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorType.USER_NOT_FOUND));

        Doctor doctor = Doctor.builder()
                .user(user)
                .specialization(request.getSpecialization())
                .phone(request.getPhone())
                .licenseNumber(request.getLicenseNumber())
                .experienceYears(request.getExperienceYears())
                .status(request.getStatus() != null ? request.getStatus() : DoctorStatus.ACTIVE)
                .active(true)
                .build();

        // O'ZGARTIRILDI: workingDays/workStartTime/workEndTime o'rniga
        // har bir kun uchun alohida DoctorSchedule yoziladi
        applySchedules(doctor, request.getSchedules());

        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Override
    @Transactional
    public DoctorResponse update(Long id, DoctorRequest request) {
        Doctor doctor = findById(id);
        doctor.setSpecialization(request.getSpecialization());
        doctor.setPhone(request.getPhone());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setExperienceYears(request.getExperienceYears());
        if (request.getStatus() != null) doctor.setStatus(request.getStatus());

        // O'ZGARTIRILDI: eski jadval to'liq tozalanib, yangisi bilan almashtiriladi.
        // orphanRemoval=true bo'lgani uchun clear() qilingan elementlar bazadan o'chadi.
        applySchedules(doctor, request.getSchedules());

        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Override
    public void delete(Long id) {
        Doctor doctor = findById(id);
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

    private Doctor findById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorType.DOCTOR_NOT_FOUND));
    }

    // YANGI: DoctorScheduleRequest ro'yxatini DoctorSchedule entity'lariga aylantirib,
    // doctor.schedules ro'yxatini to'liq qayta tuzadi.
    // Validatsiya: endTime > startTime, har bir kun faqat bir marta kelishi kerak.
    private void applySchedules(Doctor doctor, List<DoctorScheduleRequest> scheduleRequests) {
        List<DoctorSchedule> newSchedules = new ArrayList<>();

        if (scheduleRequests != null) {
            java.util.Set<java.time.DayOfWeek> seenDays = new java.util.HashSet<>();

            for (DoctorScheduleRequest sr : scheduleRequests) {
                if (!sr.getEndTime().isAfter(sr.getStartTime()))
                    throw new AppException(ErrorType.INVALID_SCHEDULE_TIME);

                if (!seenDays.add(sr.getDayOfWeek()))
                    throw new AppException(ErrorType.DUPLICATE_SCHEDULE_DAY);

                newSchedules.add(DoctorSchedule.builder()
                        .doctor(doctor)
                        .dayOfWeek(sr.getDayOfWeek())
                        .startTime(sr.getStartTime())
                        .endTime(sr.getEndTime())
                        .build());
            }
        }

        // Eski ro'yxatni tozalab, yangi elementlarni qo'shamiz —
        // shu orqali Hibernate orphanRemoval ishlaydi va eski qatorlar o'chadi.
        doctor.getSchedules().clear();
        doctor.getSchedules().addAll(newSchedules);
    }
}