package com.sports.session_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TacticSelectionRequest(
        @NotBlank String tacticCode,
        @NotBlank String tacticName
) {}
