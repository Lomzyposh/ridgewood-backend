package com.sports.team_service.service.impl;

import com.sports.team_service.dto.request.UpdateRosterRoleRequest;
import com.sports.team_service.dto.response.RosterMemberResponse;
import com.sports.team_service.entity.RosterMember;
import com.sports.team_service.entity.RosterStatus;
import com.sports.team_service.exception.ResourceNotFoundException;
import com.sports.team_service.repository.RosterMemberRepository;
import com.sports.team_service.repository.TeamRepository;
import com.sports.team_service.service.RosterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RosterServiceImpl implements RosterService {

    private final RosterMemberRepository rosterMemberRepository;
    private final TeamRepository teamRepository;

    @Override
    public List<RosterMemberResponse> getActiveRoster(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team not found with id: " + teamId);
        }

        return rosterMemberRepository.findByTeamIdAndStatus(teamId, RosterStatus.ACTIVE).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public RosterMemberResponse updateRole(Long rosterMemberId, UpdateRosterRoleRequest request) {
        RosterMember rosterMember = findRosterMember(rosterMemberId);
        rosterMember.setRole(request.getRole());
        return toResponse(rosterMemberRepository.save(rosterMember));
    }

    @Override
    public void removeFromRoster(Long rosterMemberId) {
        RosterMember rosterMember = findRosterMember(rosterMemberId);
        rosterMember.setStatus(RosterStatus.INACTIVE);
        rosterMemberRepository.save(rosterMember);
    }

    private RosterMember findRosterMember(Long rosterMemberId) {
        return rosterMemberRepository.findById(rosterMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Roster member not found with id: " + rosterMemberId));
    }

    private RosterMemberResponse toResponse(RosterMember rosterMember) {
        return RosterMemberResponse.builder()
                .id(rosterMember.getId())
                .teamId(rosterMember.getTeam().getId())
                .studentId(rosterMember.getStudentId())
                .role(rosterMember.getRole())
                .status(rosterMember.getStatus())
                .joinedAt(rosterMember.getJoinedAt())
                .build();
    }
}
