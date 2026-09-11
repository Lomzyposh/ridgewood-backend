package com.sports.session_service.dto.request;

import com.sports.session_service.entity.FitnessAvailability;
import com.sports.session_service.entity.FitnessWorkload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PlayerFitnessStatusRequest(
    @NotNull Long studentId,
    @NotNull Long teamId,
    @NotNull FitnessAvailability availability,
    @NotNull FitnessWorkload workload,
    @Size(max = 500) String conditionNote,
    @Size(max = 1500) String coachNote
) {}
