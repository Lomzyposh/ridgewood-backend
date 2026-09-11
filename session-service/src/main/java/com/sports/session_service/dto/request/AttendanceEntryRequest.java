package com.sports.session_service.dto.request;

import com.sports.session_service.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AttendanceEntryRequest(
        @NotNull @Positive Long studentId,
        @NotNull AttendanceStatus status
) {}
