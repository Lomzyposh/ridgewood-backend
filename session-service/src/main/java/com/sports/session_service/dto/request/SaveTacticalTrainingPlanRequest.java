package com.sports.session_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record SaveTacticalTrainingPlanRequest(
        @NotNull @Positive Long teamId,
        @NotNull @NotEmpty List<@Valid TacticSelectionRequest> tactics
) {}
