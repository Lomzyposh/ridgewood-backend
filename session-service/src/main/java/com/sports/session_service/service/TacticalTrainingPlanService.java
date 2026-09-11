package com.sports.session_service.service;

import com.sports.session_service.dto.request.SaveTacticalTrainingPlanRequest;
import com.sports.session_service.dto.request.TacticSelectionRequest;
import com.sports.session_service.dto.response.TacticalTrainingPlanResponse;
import com.sports.session_service.entity.Session;
import com.sports.session_service.entity.TacticalTrainingPlan;
import com.sports.session_service.exception.BadRequestException;
import com.sports.session_service.exception.ResourceNotFoundException;
import com.sports.session_service.repository.TacticalTrainingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TacticalTrainingPlanService {

    private final TacticalTrainingPlanRepository repository;
    private final SessionService sessionService;

    @Transactional
    public List<TacticalTrainingPlanResponse> replaceSessionPlan(
            Long coachId,
            Long sessionId,
            SaveTacticalTrainingPlanRequest request
    ) {
        requireCoachId(coachId);

        Session session = sessionService.getEntity(sessionId);

        if (!session.getTeamId().equals(request.teamId())) {
            throw new BadRequestException("The selected session does not belong to this team");
        }

        List<TacticSelectionRequest> selections = request.tactics();
        if (selections == null || selections.isEmpty()) {
            throw new BadRequestException("Select at least one tactic");
        }

        Set<String> seenCodes = new LinkedHashSet<>();
        for (TacticSelectionRequest tactic : selections) {
            String normalizedCode = normalizeCode(tactic.tacticCode());
            if (!seenCodes.add(normalizedCode)) {
                throw new BadRequestException(
                        "The same tactic cannot be added to a training session more than once"
                );
            }
        }

        repository.deleteByCoachIdAndSessionId(coachId, sessionId);

        List<TacticalTrainingPlan> saved = new ArrayList<>();

        for (int index = 0; index < selections.size(); index++) {
            TacticSelectionRequest tactic = selections.get(index);

            TacticalTrainingPlan row = TacticalTrainingPlan.builder()
                    .coachId(coachId)
                    .teamId(request.teamId())
                    .session(session)
                    .sport(inferSport(tactic.tacticCode()))
                    .tacticCode(normalizeCode(tactic.tacticCode()))
                    .tacticName(tactic.tacticName().trim())
                    .sortOrder(index + 1)
                    .build();

            saved.add(repository.save(row));
        }

        return saved.stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TacticalTrainingPlanResponse> getForSession(Long coachId, Long sessionId) {
        requireCoachId(coachId);

        // Fail clearly if the session itself does not exist.
        sessionService.getEntity(sessionId);

        return repository
                .findByCoachIdAndSessionIdOrderBySortOrderAsc(coachId, sessionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TacticalTrainingPlanResponse> getForTeam(Long coachId, Long teamId) {
        requireCoachId(coachId);

        return repository
                .findByCoachIdAndTeamIdOrderBySessionSessionDateAscSortOrderAsc(coachId, teamId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void clearSessionPlan(Long coachId, Long sessionId) {
        requireCoachId(coachId);
        sessionService.getEntity(sessionId);
        repository.deleteByCoachIdAndSessionId(coachId, sessionId);
    }

    @Transactional
    public void deleteOne(Long coachId, Long id) {
        requireCoachId(coachId);

        TacticalTrainingPlan plan = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tactical training plan item not found"));

        if (!plan.getCoachId().equals(coachId)) {
            throw new ResourceNotFoundException("Tactical training plan item not found");
        }

        repository.delete(plan);
    }

    private void requireCoachId(Long coachId) {
        if (coachId == null) {
            throw new BadRequestException("Authenticated coach id is missing from the token");
        }
    }

    private String normalizeCode(String code) {
        return code.trim()
                .toUpperCase()
                .replace('-', '_')
                .replace(' ', '_');
    }

    private String inferSport(String tacticCode) {
        String code = normalizeCode(tacticCode);

        return switch (code) {
            case "PICK_ROLL", "GIVE_GO", "HORNS", "FLARE",
                 "SPAIN_PNR", "FAST_BREAK" -> "BASKETBALL";
            default -> "FOOTBALL";
        };
    }

    private TacticalTrainingPlanResponse toResponse(TacticalTrainingPlan plan) {
        Session session = plan.getSession();

        return new TacticalTrainingPlanResponse(
                plan.getId(),
                plan.getCoachId(),
                plan.getTeamId(),
                session.getId(),
                session.getTitle(),
                session.getSessionDate(),
                plan.getSport(),
                plan.getTacticCode(),
                plan.getTacticName(),
                plan.getSortOrder(),
                plan.getCreatedAt()
        );
    }
}
