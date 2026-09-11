package com.sports.user_service.controller;

import com.sports.user_service.dto.response.UserResponse;
import com.sports.user_service.dto.request.UpdateUserProfileRequest;
import com.sports.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @GetMapping("/by-email")
    public UserResponse findByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}/profile")
    public UserResponse updateProfile(
            @PathVariable Long id,
            @RequestBody UpdateUserProfileRequest request) {
        return userService.updateProfile(id, request);
    }
}