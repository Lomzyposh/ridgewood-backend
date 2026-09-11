package com.sports.team_service.controller;

import com.sports.team_service.dto.request.JoinRequestCreateRequest;
import com.sports.team_service.dto.request.TeamRequest;
import com.sports.team_service.dto.response.JoinRequestResponse;
import com.sports.team_service.dto.response.MessageResponse;
import com.sports.team_service.dto.response.RosterMemberResponse;
import com.sports.team_service.dto.response.TeamResponse;
import com.sports.team_service.service.JoinRequestService;
import com.sports.team_service.service.RosterService;
import com.sports.team_service.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final JoinRequestService joinRequestService;
    private final RosterService rosterService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public TeamResponse createTeam(@Valid @RequestBody TeamRequest request) {
        return teamService.createTeam(request);
    }

    @GetMapping
    public List<TeamResponse> getAllTeams() {
        return teamService.getAllTeams();
    }

    @GetMapping("/coach/{coachId}")
    public List<TeamResponse> getTeamsByCoach(@PathVariable Long coachId) {
        return teamService.getTeamsByCoach(coachId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public TeamResponse updateTeam(@PathVariable Long id, @Valid @RequestBody TeamRequest request) {
        return teamService.updateTeam(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    public MessageResponse deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return new MessageResponse("Team deleted successfully");
    }

    @PostMapping("/{id}/join-requests")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public JoinRequestResponse requestToJoin(@PathVariable Long id,
                                             @Valid @RequestBody JoinRequestCreateRequest request) {
        return joinRequestService.requestToJoin(id, request);
    }

    @GetMapping("/{id}/join-requests")
    public List<JoinRequestResponse> getJoinRequests(@PathVariable Long id) {
        return joinRequestService.getByTeam(id);
    }

    @GetMapping("/{id}/roster")
    public List<RosterMemberResponse> getActiveRoster(@PathVariable Long id) {
        return rosterService.getActiveRoster(id);
    }
}
