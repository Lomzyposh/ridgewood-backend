package com.sports.facility_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FacilityRequest(
        @NotBlank String name,
        @NotBlank String type
) {}
