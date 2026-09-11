package com.sports.session_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public record BulkAttendanceRequest(
        @NotNull @Positive Long sessionId,
        LocalDate date,
        @NotEmpty List<@Valid AttendanceEntryRequest> attendance
) {}
