package com.sports.team_service.service.impl;

import com.sports.team_service.dto.request.JoinRequestCreateRequest;
import com.sports.team_service.dto.response.JoinRequestResponse;
import com.sports.team_service.entity.JoinRequest;
import com.sports.team_service.entity.JoinRequestStatus;
import com.sports.team_service.entity.RosterMember;
import com.sports.team_service.entity.RosterStatus;
import com.sports.team_service.entity.Sport;
import com.sports.team_service.entity.Team;
import com.sports.team_service.exception.BadRequestException;
import com.sports.team_service.repository.JoinRequestRepository;
import com.sports.team_service.repository.RosterMemberRepository;
import com.sports.team_service.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoinRequestServiceImplTest {

    @Mock
    private JoinRequestRepository joinRequestRepository;

    @Mock
    private RosterMemberRepository rosterMemberRepository;

    @Mock
    private TeamRepository teamRepository;

    private JoinRequestServiceImpl service;
    private Team manU;
    private Team lomzy;

    @BeforeEach
    void setUp() {
        service = new JoinRequestServiceImpl(
                joinRequestRepository,
                rosterMemberRepository,
                teamRepository
        );

        manU = Team.builder()
                .id(1L)
                .name("Man U")
                .sport(Sport.FOOTBALL)
                .coachId(10L)
                .build();

        lomzy = Team.builder()
                .id(2L)
                .name("Lomzy Basketball Squad")
                .sport(Sport.BASKETBALL)
                .coachId(20L)
                .build();
    }

    @Test
    void studentCanRequestToJoinAnotherTeam() {
        JoinRequestCreateRequest request = new JoinRequestCreateRequest();
        request.setStudentId(7L);
        request.setCoachInvite(false);

        when(teamRepository.findById(2L)).thenReturn(Optional.of(lomzy));
        when(rosterMemberRepository.findByStudentIdAndStatus(7L, RosterStatus.ACTIVE))
                .thenReturn(List.of());
        when(rosterMemberRepository.findByTeamAndStudentId(lomzy, 7L))
                .thenReturn(Optional.empty());
        when(joinRequestRepository.findByTeamAndStudentId(lomzy, 7L))
                .thenReturn(Optional.empty());
        when(joinRequestRepository.save(any(JoinRequest.class)))
                .thenAnswer(invocation -> {
                    JoinRequest saved = invocation.getArgument(0);
                    saved.setId(100L);
                    return saved;
                });

        JoinRequestResponse response = service.requestToJoin(2L, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(2L, response.getTeamId());
        assertEquals(7L, response.getStudentId());
        assertEquals(JoinRequestStatus.PENDING, response.getStatus());
        verify(joinRequestRepository).save(any(JoinRequest.class));
    }

    @Test
    void studentCannotRequestSameTeamWhenAlreadyActiveThere() {
        JoinRequestCreateRequest request = new JoinRequestCreateRequest();
        request.setStudentId(7L);

        RosterMember activeLomzyMember = RosterMember.builder()
                .id(50L)
                .team(lomzy)
                .studentId(7L)
                .role("Player")
                .status(RosterStatus.ACTIVE)
                .build();

        when(teamRepository.findById(2L)).thenReturn(Optional.of(lomzy));
        when(rosterMemberRepository.findByStudentIdAndStatus(7L, RosterStatus.ACTIVE))
                .thenReturn(List.of(activeLomzyMember));
        when(rosterMemberRepository.findByTeamAndStudentId(lomzy, 7L))
                .thenReturn(Optional.of(activeLomzyMember));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.requestToJoin(2L, request)
        );

        assertEquals("Student is already an active roster member", exception.getMessage());
        verify(joinRequestRepository, never()).save(any());
    }

    @Test
    void studentCannotCreateDuplicatePendingRequestForSameTeam() {
        JoinRequestCreateRequest request = new JoinRequestCreateRequest();
        request.setStudentId(7L);

        JoinRequest pending = JoinRequest.builder()
                .id(70L)
                .team(lomzy)
                .studentId(7L)
                .coachInvite(false)
                .status(JoinRequestStatus.PENDING)
                .build();

        when(teamRepository.findById(2L)).thenReturn(Optional.of(lomzy));
        when(rosterMemberRepository.findByStudentIdAndStatus(7L, RosterStatus.ACTIVE))
                .thenReturn(List.of());
        when(rosterMemberRepository.findByTeamAndStudentId(lomzy, 7L))
                .thenReturn(Optional.empty());
        when(joinRequestRepository.findByTeamAndStudentId(lomzy, 7L))
                .thenReturn(Optional.of(pending));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.requestToJoin(2L, request)
        );

        assertEquals("Student already has a pending join request for this team", exception.getMessage());
        verify(joinRequestRepository, never()).save(any());
    }

    @Test
    void approvingTransferDeactivatesOldTeamAndActivatesNewTeam() {
        JoinRequest transferRequest = JoinRequest.builder()
                .id(80L)
                .team(lomzy)
                .studentId(7L)
                .coachInvite(false)
                .status(JoinRequestStatus.PENDING)
                .build();

        RosterMember oldMembership = RosterMember.builder()
                .id(90L)
                .team(manU)
                .studentId(7L)
                .role("Player")
                .status(RosterStatus.ACTIVE)
                .build();

        when(joinRequestRepository.findById(80L)).thenReturn(Optional.of(transferRequest));
        when(rosterMemberRepository.findByStudentIdAndStatus(7L, RosterStatus.ACTIVE))
                .thenReturn(List.of(oldMembership));
        when(rosterMemberRepository.findByTeamAndStudentId(lomzy, 7L))
                .thenReturn(Optional.empty());
        when(rosterMemberRepository.save(any(RosterMember.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(joinRequestRepository.save(any(JoinRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        JoinRequestResponse response = service.approveJoinRequest(80L);

        assertEquals(RosterStatus.INACTIVE, oldMembership.getStatus());
        assertEquals(JoinRequestStatus.APPROVED, response.getStatus());

        verify(rosterMemberRepository, times(2)).save(any(RosterMember.class));
        verify(joinRequestRepository).save(transferRequest);
    }

    @Test
    void nonPendingJoinRequestCannotBeApprovedAgain() {
        JoinRequest alreadyApproved = JoinRequest.builder()
                .id(81L)
                .team(lomzy)
                .studentId(7L)
                .coachInvite(false)
                .status(JoinRequestStatus.APPROVED)
                .build();

        when(joinRequestRepository.findById(81L)).thenReturn(Optional.of(alreadyApproved));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.approveJoinRequest(81L)
        );

        assertEquals("Only pending join requests can be updated", exception.getMessage());
        verify(rosterMemberRepository, never()).save(any());
    }
}
