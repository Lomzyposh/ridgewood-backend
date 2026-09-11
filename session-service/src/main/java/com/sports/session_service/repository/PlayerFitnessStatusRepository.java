package com.sports.session_service.repository;

import com.sports.session_service.entity.PlayerFitnessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PlayerFitnessStatusRepository
        extends JpaRepository<PlayerFitnessStatus, Long> {

    List<PlayerFitnessStatus> findByCoachIdAndTeamIdOrderByUpdatedAtDesc(
        Long coachId, Long teamId
    );

    Optional<PlayerFitnessStatus> findByCoachIdAndStudentIdAndTeamId(
        Long coachId, Long studentId, Long teamId
    );

    Optional<PlayerFitnessStatus> findByStudentIdAndTeamId(
        Long studentId, Long teamId
    );
}
