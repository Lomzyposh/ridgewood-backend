package com.sports.session_service.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record SessionResponse(
        Long id,
        Long teamId,
        String title,
        LocalDate sessionDate,
        LocalTime startTime,
        LocalTime endTime,
        LocalDateTime createdAt
) {}
