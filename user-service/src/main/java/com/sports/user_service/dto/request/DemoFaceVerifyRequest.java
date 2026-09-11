package com.sports.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DemoFaceVerifyRequest(
        @NotBlank String resetSession
) {}
