package com.sports.session_service.dto.response;

import com.sports.session_service.entity.FitnessAvailability;
import com.sports.session_service.entity.FitnessWorkload;
import java.time.LocalDateTime;

public record PlayerFitnessStatusResponse(
    Long id,
    Long coachId,
    Long studentId,
    Long teamId,
    FitnessAvailability availability,
    FitnessWorkload workload,
    String conditionNote,
    String coachNote,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
