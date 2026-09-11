package com.sports.session_service.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TacticalTrainingPlanResponse(
        Long id,
        Long coachId,
        Long teamId,
        Long sessionId,
        String sessionTitle,
        LocalDate sessionDate,
        String sport,
        String tacticCode,
        String tacticName,
        Integer sortOrder,
        LocalDateTime createdAt
) {}
