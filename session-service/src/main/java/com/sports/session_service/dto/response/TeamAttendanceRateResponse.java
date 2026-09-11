package com.sports.session_service.dto.response;

public record TeamAttendanceRateResponse(
        Long teamId,
        long totalRecords,
        long presentRecords,
        double attendanceRate
) {}
