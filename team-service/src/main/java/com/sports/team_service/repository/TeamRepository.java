package com.sports.team_service.repository;

import com.sports.team_service.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByCoachId(Long coachId);
}
