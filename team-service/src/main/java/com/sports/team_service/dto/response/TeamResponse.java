package com.sports.team_service.dto.response;

import com.sports.team_service.entity.Sport;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeamResponse {

    private Long id;
    private String name;
    private Sport sport;
    private Long coachId;
    private String imageUrl;
    private LocalDateTime createdAt;
}
