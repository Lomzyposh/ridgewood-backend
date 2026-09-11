package com.sports.team_service.dto.response;

import com.sports.team_service.entity.JoinRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JoinRequestResponse {

    private Long id;
    private Long teamId;
    private Long studentId;
    private boolean coachInvite;
    private JoinRequestStatus status;
    private LocalDateTime requestedAt;
}
