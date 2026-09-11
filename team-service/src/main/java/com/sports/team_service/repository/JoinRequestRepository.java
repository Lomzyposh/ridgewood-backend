package com.sports.team_service.repository;

import com.sports.team_service.entity.JoinRequest;
import com.sports.team_service.entity.JoinRequestStatus;
import com.sports.team_service.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {

    Optional<JoinRequest> findByTeamAndStudentId(Team team, Long studentId);

    boolean existsByTeamAndStudentIdAndStatus(Team team, Long studentId, JoinRequestStatus status);

    List<JoinRequest> findByTeamId(Long teamId);

    List<JoinRequest> findByStudentId(Long studentId);

    Optional<JoinRequest> findTopByStudentIdAndStatusOrderByIdDesc(Long studentId, JoinRequestStatus status);

    List<JoinRequest> findByTeamCoachId(Long coachId);
}
