package com.sports.team_service.dto.request;

import com.sports.team_service.entity.Sport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRequest {

    @NotBlank(message = "Team name is required")
    private String name;

    @NotNull(message = "Sport is required")
    private Sport sport;

    @NotNull(message = "Coach ID is required")
    private Long coachId;

    private String imageUrl;
}
