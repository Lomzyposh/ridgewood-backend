package com.sports.session_service.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SessionFeedbackResponse(
        Long id,
        Long sessionId,
        Long teamId,
        String sessionTitle,
        LocalDate sessionDate,
        Long studentId,
        Integer rating,
        String mood,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
