package com.sports.team_service.service;

import com.sports.team_service.dto.request.TeamRequest;
import com.sports.team_service.dto.response.TeamResponse;

import java.util.List;

public interface TeamService {

    TeamResponse createTeam(TeamRequest request);

    List<TeamResponse> getAllTeams();

    List<TeamResponse> getTeamsByCoach(Long coachId);

    TeamResponse updateTeam(Long id, TeamRequest request);

    void deleteTeam(Long id);
}
