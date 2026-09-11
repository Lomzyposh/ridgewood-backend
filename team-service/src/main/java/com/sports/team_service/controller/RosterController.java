package com.sports.team_service.controller;

import com.sports.team_service.dto.request.UpdateRosterRoleRequest;
import com.sports.team_service.dto.response.MessageResponse;
import com.sports.team_service.dto.response.RosterMemberResponse;
import com.sports.team_service.service.RosterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roster")
@RequiredArgsConstructor
public class RosterController {

    private final RosterService rosterService;

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public RosterMemberResponse updateRole(@PathVariable Long id,
                                           @Valid @RequestBody UpdateRosterRoleRequest request) {
        return rosterService.updateRole(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public MessageResponse removeFromRoster(@PathVariable Long id) {
        rosterService.removeFromRoster(id);
        return new MessageResponse("Roster member removed successfully");
    }
}
