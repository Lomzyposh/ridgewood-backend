package com.sports.team_service.service.impl;

import com.sports.team_service.dto.request.TeamRequest;
import com.sports.team_service.dto.response.TeamResponse;
import com.sports.team_service.entity.Team;
import com.sports.team_service.exception.ResourceNotFoundException;
import com.sports.team_service.repository.TeamRepository;
import com.sports.team_service.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    @Override
    public TeamResponse createTeam(TeamRequest request) {
        Team team = Team.builder()
                .name(request.getName())
                .sport(request.getSport())
                .coachId(request.getCoachId())
                .imageUrl(request.getImageUrl())
                .build();

        return toResponse(teamRepository.save(team));
    }

    @Override
    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<TeamResponse> getTeamsByCoach(Long coachId) {
        return teamRepository.findByCoachId(coachId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public TeamResponse updateTeam(Long id, TeamRequest request) {
        Team team = findTeam(id);
        team.setName(request.getName());
        team.setSport(request.getSport());
        team.setCoachId(request.getCoachId());
        team.setImageUrl(request.getImageUrl());
        return toResponse(teamRepository.save(team));
    }

    @Override
    public void deleteTeam(Long id) {
        Team team = findTeam(id);
        teamRepository.delete(team);
    }

    private Team findTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
    }

    private TeamResponse toResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .sport(team.getSport())
                .coachId(team.getCoachId())
                .imageUrl(team.getImageUrl())
                .createdAt(team.getCreatedAt())
                .build();
    }
}
