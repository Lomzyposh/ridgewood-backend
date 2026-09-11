package com.sports.session_service.repository;

import com.sports.session_service.entity.SessionFeedback;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionFeedbackRepository extends JpaRepository<SessionFeedback, Long> {

    @EntityGraph(attributePaths = "session")
    Optional<SessionFeedback> findBySessionIdAndStudentId(Long sessionId, Long studentId);

    @EntityGraph(attributePaths = "session")
    List<SessionFeedback> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    @EntityGraph(attributePaths = "session")
    List<SessionFeedback> findBySessionIdOrderByCreatedAtDesc(Long sessionId);

    @EntityGraph(attributePaths = "session")
    List<SessionFeedback> findBySessionTeamIdOrderByCreatedAtDesc(Long teamId);
}
