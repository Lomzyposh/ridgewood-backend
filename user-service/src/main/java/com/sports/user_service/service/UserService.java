package com.sports.user_service.service;

import com.sports.user_service.dto.request.LoginRequest;
import com.sports.user_service.dto.request.SignupRequest;
import com.sports.user_service.dto.request.UpdateUserProfileRequest;
import com.sports.user_service.dto.response.AuthResponse;
import com.sports.user_service.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);

    List<UserResponse> findAll();

    UserResponse findByEmail(String email);

    UserResponse findById(Long id);

    UserResponse updateProfile(Long id, UpdateUserProfileRequest request);
}