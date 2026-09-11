package com.sports.session_service.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SessionFeedbackRequest(
        @NotNull Long sessionId,
        @NotNull @Min(1) @Max(5) Integer rating,
        @NotBlank @Size(max = 40) String mood,
        @NotBlank @Size(max = 2000) String comment
) {}
