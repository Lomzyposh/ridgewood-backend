package com.sports.team_service.repository;

import com.sports.team_service.entity.RosterMember;
import com.sports.team_service.entity.RosterStatus;
import com.sports.team_service.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RosterMemberRepository extends JpaRepository<RosterMember, Long> {

    List<RosterMember> findByTeamIdAndStatus(Long teamId, RosterStatus status);

    Optional<RosterMember> findByTeamAndStudentId(Team team, Long studentId);

    List<RosterMember> findByStudentIdAndStatus(Long studentId, RosterStatus status);
}
