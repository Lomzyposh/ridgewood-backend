package com.sports.session_service.repository;

import com.sports.session_service.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByTeamIdOrderBySessionDateAscStartTimeAsc(Long teamId);
}
