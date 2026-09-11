package com.sports.session_service.service;

import com.sports.session_service.dto.request.PlayerFitnessStatusRequest;
import com.sports.session_service.dto.response.PlayerFitnessStatusResponse;
import com.sports.session_service.entity.PlayerFitnessStatus;
import com.sports.session_service.exception.BadRequestException;
import com.sports.session_service.exception.ResourceNotFoundException;
import com.sports.session_service.repository.PlayerFitnessStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerFitnessStatusService {

    private final PlayerFitnessStatusRepository repository;

    public List<PlayerFitnessStatusResponse> getTeamStatuses(Long coachId, Long teamId) {
        validateId(coachId, "Authenticated coach id is missing from the token");
        validateId(teamId, "Team id is required");

        return repository.findByCoachIdAndTeamIdOrderByUpdatedAtDesc(coachId, teamId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public PlayerFitnessStatusResponse getStudentStatus(Long studentId, Long teamId) {
        validateId(studentId, "Authenticated student id is missing from the token");
        validateId(teamId, "Team id is required");

        return repository.findByStudentIdAndTeamId(studentId, teamId)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException(
                "No fitness status has been recorded for this student"
            ));
    }

    @Transactional
    public PlayerFitnessStatusResponse saveOrUpdate(
        Long coachId,
        Long studentId,
        Long teamId,
        PlayerFitnessStatusRequest request
    ) {
        validateId(coachId, "Authenticated coach id is missing from the token");
        validateId(studentId, "Student id is required");
        validateId(teamId, "Team id is required");

        if (!studentId.equals(request.studentId())) {
            throw new BadRequestException(
                "Student id in the URL does not match the request body"
            );
        }

        if (!teamId.equals(request.teamId())) {
            throw new BadRequestException(
                "Team id in the URL does not match the request body"
            );
        }

        PlayerFitnessStatus status = repository
            .findByCoachIdAndStudentIdAndTeamId(coachId, studentId, teamId)
            .orElseGet(() -> PlayerFitnessStatus.builder()
                .coachId(coachId)
                .studentId(studentId)
                .teamId(teamId)
                .build());

        status.setAvailability(request.availability());
        status.setWorkload(request.workload());
        status.setConditionNote(clean(request.conditionNote()));
        status.setCoachNote(clean(request.coachNote()));

        return toResponse(repository.save(status));
    }

    private void validateId(Long value, String message) {
        if (value == null || value <= 0) {
            throw new BadRequestException(message);
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private PlayerFitnessStatusResponse toResponse(PlayerFitnessStatus status) {
        return new PlayerFitnessStatusResponse(
            status.getId(),
            status.getCoachId(),
            status.getStudentId(),
            status.getTeamId(),
            status.getAvailability(),
            status.getWorkload(),
            status.getConditionNote(),
            status.getCoachNote(),
            status.getCreatedAt(),
            status.getUpdatedAt()
        );
    }
}
