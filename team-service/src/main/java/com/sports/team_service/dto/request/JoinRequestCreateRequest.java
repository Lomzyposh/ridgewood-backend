package com.sports.team_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequestCreateRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private boolean coachInvite;
}
