package com.sports.session_service.dto.response;

import com.sports.session_service.entity.AttendanceStatus;

import java.time.LocalDate;

public record AttendanceResponse(
        Long id,
        Long sessionId,
        Long teamId,
        Long studentId,
        LocalDate date,
        AttendanceStatus status
) {}
