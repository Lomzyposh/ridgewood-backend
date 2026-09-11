package com.sports.session_service.repository;

import com.sports.session_service.entity.TacticalTrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TacticalTrainingPlanRepository extends JpaRepository<TacticalTrainingPlan, Long> {

    List<TacticalTrainingPlan> findByCoachIdAndSessionIdOrderBySortOrderAsc(
            Long coachId,
            Long sessionId
    );

    List<TacticalTrainingPlan> findByCoachIdAndTeamIdOrderBySessionSessionDateAscSortOrderAsc(
            Long coachId,
            Long teamId
    );

    void deleteByCoachIdAndSessionId(Long coachId, Long sessionId);
}
