package com.sports.session_service.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CoachPlayerNoteResponse(
        Long id,
        Long coachId,
        Long studentId,
        Long teamId,
        Long sessionId,
        String sessionTitle,
        LocalDate sessionDate,
        String focus,
        String note,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
