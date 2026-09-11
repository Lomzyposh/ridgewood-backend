package com.sports.facility_service.dto.response;

import java.time.*;

public record BookingResponse(
        Long id,
        Long facilityId,
        String facilityName,
        Long teamId,
        DayOfWeek day,
        LocalTime startTime,
        LocalTime endTime,
        LocalDate date,
        LocalDateTime createdAt
) {}
