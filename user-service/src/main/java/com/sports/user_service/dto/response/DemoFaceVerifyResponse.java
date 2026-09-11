package com.sports.user_service.dto.response;

public record DemoFaceVerifyResponse(
        boolean verified,
        String message
) {}
