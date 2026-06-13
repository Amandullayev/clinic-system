package uz.clinic.service;

import uz.clinic.dto.request.UserCreateRequest;
import uz.clinic.dto.request.UserUpdateRequest;
import uz.clinic.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAll();
    UserResponse getById(Long id);
    UserResponse create(UserCreateRequest request);
    UserResponse update(Long id, UserUpdateRequest request);
    void delete(Long id);
    UserResponse toggleActive(Long id);
}