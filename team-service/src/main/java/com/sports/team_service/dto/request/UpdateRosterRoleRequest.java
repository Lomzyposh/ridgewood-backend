package com.sports.team_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRosterRoleRequest {

    @NotBlank(message = "Role is required")
    private String role;
}
