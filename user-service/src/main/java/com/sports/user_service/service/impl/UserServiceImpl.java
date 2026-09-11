package com.sports.user_service.service.impl;

import com.sports.user_service.repository.UserRepository;
import com.sports.user_service.dto.request.LoginRequest;
import com.sports.user_service.dto.request.SignupRequest;
import com.sports.user_service.dto.request.UpdateUserProfileRequest;
import com.sports.user_service.dto.response.AuthResponse;
import com.sports.user_service.dto.response.UserResponse;
import com.sports.user_service.entity.AccountStatus;
import com.sports.user_service.entity.Role;
import com.sports.user_service.entity.User;
import com.sports.user_service.exception.InvalidCredentialsException;
import com.sports.user_service.exception.UserAlreadyExistsException;
import com.sports.user_service.exception.ResourceNotFoundException;
import com.sports.user_service.security.JwtUtil;
import com.sports.user_service.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "An account with this email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .status(AccountStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getId());

        return AuthResponse.builder()
                .token(token)
                .user(mapToResponse(savedUser))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new InvalidCredentialsException(
                    "Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId());

        return AuthResponse.builder()
                .token(token)
                .user(mapToResponse(user))
                .build();
    }

    @Override
    public List<UserResponse> findAll() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse findByEmail(String email) {

        return mapToResponse(
                userRepository.findByEmail(email.toLowerCase())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Student account not found")));
    }

    @Override
    public UserResponse findById(Long id) {

        return mapToResponse(
                userRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "User account not found")));
    }

    @Override
    public UserResponse updateProfile(
            Long id,
            UpdateUserProfileRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User account not found"));

        if (request.getProfilePhotoUrl() != null) {
            user.setProfilePhotoUrl(
                    request.getProfilePhotoUrl());
        }

        if (request.getHeightCm() != null) {
            user.setHeightCm(
                    request.getHeightCm());
        }

        if (request.getWeightKg() != null) {
            user.setWeightKg(
                    request.getWeightKg());
        }

        return mapToResponse(
                userRepository.save(user));
    }

    private UserResponse mapToResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .paymentStatus(user.getPaymentStatus())
                .teamId(user.getTeamId())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .heightCm(user.getHeightCm())
                .weightKg(user.getWeightKg())
                .createdAt(user.getCreatedAt())
                .build();
    }
}