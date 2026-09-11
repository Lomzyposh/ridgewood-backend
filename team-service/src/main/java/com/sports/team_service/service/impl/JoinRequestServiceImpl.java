package com.sports.team_service.service.impl;

import com.sports.team_service.dto.request.JoinRequestCreateRequest;
import com.sports.team_service.dto.response.JoinRequestResponse;
import com.sports.team_service.entity.JoinRequest;
import com.sports.team_service.entity.JoinRequestStatus;
import com.sports.team_service.entity.RosterMember;
import com.sports.team_service.entity.RosterStatus;
import com.sports.team_service.entity.Team;
import com.sports.team_service.exception.BadRequestException;
import com.sports.team_service.exception.ResourceNotFoundException;
import com.sports.team_service.repository.JoinRequestRepository;
import com.sports.team_service.repository.RosterMemberRepository;
import com.sports.team_service.repository.TeamRepository;
import com.sports.team_service.service.JoinRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JoinRequestServiceImpl implements JoinRequestService {

    private static final String DEFAULT_ROLE = "Player";

    private final JoinRequestRepository joinRequestRepository;
    private final RosterMemberRepository rosterMemberRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public JoinRequestResponse requestToJoin(Long teamId, JoinRequestCreateRequest request) {
        Team team = findTeam(teamId);

        // Repair duplicate ACTIVE memberships left by the old transfer logic.
        normalizeActiveMemberships(request.getStudentId());

        rosterMemberRepository.findByTeamAndStudentId(team, request.getStudentId())
                .filter(member -> member.getStatus() == RosterStatus.ACTIVE)
                .ifPresent(member -> {
                    throw new BadRequestException("Student is already an active roster member");
                });

        joinRequestRepository.findByTeamAndStudentId(team, request.getStudentId())
                .filter(joinRequest -> joinRequest.getStatus() == JoinRequestStatus.PENDING)
                .ifPresent(joinRequest -> {
                    throw new BadRequestException("Student already has a pending join request for this team");
                });

        JoinRequest joinRequest = JoinRequest.builder()
                .team(team)
                .studentId(request.getStudentId())
                .coachInvite(request.isCoachInvite())
                .status(JoinRequestStatus.PENDING)
                .build();

        return toResponse(joinRequestRepository.save(joinRequest));
    }

    @Override
    public List<JoinRequestResponse> getByTeam(Long teamId) {
        return joinRequestRepository.findByTeamId(teamId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<JoinRequestResponse> getByStudent(Long studentId) {
        return joinRequestRepository.findByStudentId(studentId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<JoinRequestResponse> getByCoach(Long coachId) {
        return joinRequestRepository.findByTeamCoachId(coachId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public JoinRequestResponse approveJoinRequest(Long id) {
        JoinRequest joinRequest = findJoinRequest(id);
        requirePending(joinRequest);

        joinRequest.setStatus(JoinRequestStatus.APPROVED);

        // Transfer approval: the target team becomes the student's only ACTIVE team.
        rosterMemberRepository.findByStudentIdAndStatus(joinRequest.getStudentId(), RosterStatus.ACTIVE)
                .stream()
                .filter(member -> !member.getTeam().getId().equals(joinRequest.getTeam().getId()))
                .forEach(member -> {
                    member.setStatus(RosterStatus.INACTIVE);
                    rosterMemberRepository.save(member);
                });

        RosterMember rosterMember = rosterMemberRepository.findByTeamAndStudentId(
                        joinRequest.getTeam(),
                        joinRequest.getStudentId()
                )
                .orElseGet(() -> RosterMember.builder()
                        .team(joinRequest.getTeam())
                        .studentId(joinRequest.getStudentId())
                        .role(DEFAULT_ROLE)
                        .build());

        rosterMember.setStatus(RosterStatus.ACTIVE);
        if (rosterMember.getRole() == null || rosterMember.getRole().isBlank()) {
            rosterMember.setRole(DEFAULT_ROLE);
        }
        rosterMemberRepository.save(rosterMember);

        return toResponse(joinRequestRepository.save(joinRequest));
    }

    @Override
    public JoinRequestResponse rejectJoinRequest(Long id) {
        JoinRequest joinRequest = findJoinRequest(id);
        requirePending(joinRequest);
        joinRequest.setStatus(JoinRequestStatus.REJECTED);
        return toResponse(joinRequestRepository.save(joinRequest));
    }

    private void normalizeActiveMemberships(Long studentId) {
        List<RosterMember> activeMemberships =
                rosterMemberRepository.findByStudentIdAndStatus(studentId, RosterStatus.ACTIVE);

        if (activeMemberships.size() <= 1) return;

        joinRequestRepository
                .findTopByStudentIdAndStatusOrderByIdDesc(studentId, JoinRequestStatus.APPROVED)
                .ifPresent(latestApproved -> {
                    Long currentTeamId = latestApproved.getTeam().getId();
                    activeMemberships.stream()
                            .filter(member -> !member.getTeam().getId().equals(currentTeamId))
                            .forEach(member -> {
                                member.setStatus(RosterStatus.INACTIVE);
                                rosterMemberRepository.save(member);
                            });
                });
    }

    private Team findTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
    }

    private JoinRequest findJoinRequest(Long id) {
        return joinRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Join request not found with id: " + id));
    }

    private void requirePending(JoinRequest joinRequest) {
        if (joinRequest.getStatus() != JoinRequestStatus.PENDING) {
            throw new BadRequestException("Only pending join requests can be updated");
        }
    }

    private JoinRequestResponse toResponse(JoinRequest joinRequest) {
        return JoinRequestResponse.builder()
                .id(joinRequest.getId())
                .teamId(joinRequest.getTeam().getId())
                .studentId(joinRequest.getStudentId())
                .coachInvite(joinRequest.isCoachInvite())
                .status(joinRequest.getStatus())
                .requestedAt(joinRequest.getRequestedAt())
                .build();
    }
}
