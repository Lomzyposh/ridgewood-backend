package com.sports.session_service.repository;

import com.sports.session_service.entity.CoachPlayerNote;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoachPlayerNoteRepository extends JpaRepository<CoachPlayerNote, Long> {

    @EntityGraph(attributePaths = "session")
    List<CoachPlayerNote> findByCoachIdAndTeamIdOrderByCreatedAtDesc(Long coachId, Long teamId);

    @EntityGraph(attributePaths = "session")
    List<CoachPlayerNote> findByCoachIdAndStudentIdOrderByCreatedAtDesc(Long coachId, Long studentId);

    @EntityGraph(attributePaths = "session")
    Optional<CoachPlayerNote> findByIdAndCoachId(Long id, Long coachId);
}
