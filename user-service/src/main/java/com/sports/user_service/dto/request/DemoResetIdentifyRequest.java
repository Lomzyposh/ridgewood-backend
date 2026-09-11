package com.sports.user_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DemoResetIdentifyRequest(
        @NotNull @Positive Long accountId
) {}
