package com.sports.user_service.dto.response;

public record DemoResetIdentifyResponse(
        String resetSession,
        Long accountId,
        String fullName,
        String maskedEmail,
        String role,
        String message
) {}
