package com.sports.team_service.dto.response;

import com.sports.team_service.entity.RosterStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RosterMemberResponse {

    private Long id;
    private Long teamId;
    private Long studentId;
    private String role;
    private RosterStatus status;
    private LocalDateTime joinedAt;
}
