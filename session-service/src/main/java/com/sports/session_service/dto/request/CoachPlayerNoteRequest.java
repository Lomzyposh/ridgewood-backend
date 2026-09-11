package com.sports.session_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CoachPlayerNoteRequest(
        @NotNull Long studentId,
        @NotNull Long teamId,
        Long sessionId,
        @NotBlank @Size(max = 60) String focus,
        @NotBlank @Size(max = 3000) String note
) {}
