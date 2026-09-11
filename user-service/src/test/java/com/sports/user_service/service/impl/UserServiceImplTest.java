package com.sports.user_service.service.impl;

import com.sports.user_service.dto.request.LoginRequest;
import com.sports.user_service.dto.request.SignupRequest;
import com.sports.user_service.dto.request.UpdateUserProfileRequest;
import com.sports.user_service.dto.response.AuthResponse;
import com.sports.user_service.dto.response.UserResponse;
import com.sports.user_service.entity.AccountStatus;
import com.sports.user_service.entity.Role;
import com.sports.user_service.entity.User;
import com.sports.user_service.exception.InvalidCredentialsException;
import com.sports.user_service.exception.ResourceNotFoundException;
import com.sports.user_service.exception.UserAlreadyExistsException;
import com.sports.user_service.repository.UserRepository;
import com.sports.user_service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void signupCreatesUserAndReturnsToken() {
        SignupRequest request = new SignupRequest();
        request.setFullName("Chris Brown");
        request.setEmail("chris@example.com");
        request.setPassword("password123");
        request.setRole("STUDENT");

        when(userRepository.existsByEmail("chris@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        User savedUser = User.builder()
                .id(7L)
                .fullName("Chris Brown")
                .email("chris@example.com")
                .password("encoded-password")
                .role(Role.STUDENT)
                .status(AccountStatus.ACTIVE)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken("chris@example.com", "STUDENT", 7L))
                .thenReturn("test-token");

        AuthResponse response = userService.signup(request);

        assertEquals("test-token", response.getToken());
        assertEquals(7L, response.getUser().getId());
        assertEquals("Chris Brown", response.getUser().getFullName());
        assertEquals(Role.STUDENT, response.getUser().getRole());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void signupRejectsDuplicateEmail() {
        SignupRequest request = new SignupRequest();
        request.setFullName("Chris Brown");
        request.setEmail("chris@example.com");
        request.setPassword("password123");
        request.setRole("STUDENT");

        when(userRepository.existsByEmail("chris@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> userService.signup(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginReturnsTokenWhenCredentialsAreCorrect() {
        LoginRequest request = new LoginRequest();
        request.setEmail("coach@example.com");
        request.setPassword("secret123");

        User user = User.builder()
                .id(3L)
                .fullName("Coach Alex")
                .email("coach@example.com")
                .password("encoded-secret")
                .role(Role.COACH)
                .status(AccountStatus.ACTIVE)
                .build();

        when(userRepository.findByEmail("coach@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "encoded-secret"))
                .thenReturn(true);
        when(jwtUtil.generateToken("coach@example.com", "COACH", 3L))
                .thenReturn("coach-token");

        AuthResponse response = userService.login(request);

        assertEquals("coach-token", response.getToken());
        assertEquals(Role.COACH, response.getUser().getRole());
        assertEquals(3L, response.getUser().getId());
    }

    @Test
    void loginRejectsWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("student@example.com");
        request.setPassword("wrong-password");

        User user = User.builder()
                .id(7L)
                .fullName("Chris Brown")
                .email("student@example.com")
                .password("encoded-correct-password")
                .role(Role.STUDENT)
                .status(AccountStatus.ACTIVE)
                .build();

        when(userRepository.findByEmail("student@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-correct-password"))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(request));

        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyLong());
    }

    @Test
    void updateProfileUpdatesStudentPhysicalDetails() {
        User user = User.builder()
                .id(7L)
                .fullName("Chris Brown")
                .email("student@example.com")
                .password("encoded")
                .role(Role.STUDENT)
                .status(AccountStatus.ACTIVE)
                .build();

        UpdateUserProfileRequest request =
                new UpdateUserProfileRequest("https://example.com/chris.jpg", 182, 76);

        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.updateProfile(7L, request);

        assertEquals("https://example.com/chris.jpg", response.getProfilePhotoUrl());
        assertEquals(182, response.getHeightCm());
        assertEquals(76, response.getWeightKg());

        verify(userRepository).save(user);
    }

    @Test
    void findByIdRejectsUnknownUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.findById(999L));
    }
}
