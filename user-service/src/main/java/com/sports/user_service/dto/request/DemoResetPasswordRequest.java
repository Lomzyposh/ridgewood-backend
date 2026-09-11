package com.sports.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DemoResetPasswordRequest(
        @NotBlank String resetSession,
        @NotBlank @Size(min = 6, max = 100) String newPassword,
        @NotBlank String confirmPassword
) {}
