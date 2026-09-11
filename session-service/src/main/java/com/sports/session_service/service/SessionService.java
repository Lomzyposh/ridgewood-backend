package com.sports.session_service.service;

import com.sports.session_service.dto.request.CreateSessionRequest;
import com.sports.session_service.dto.request.UpdateSessionRequest;
import com.sports.session_service.dto.response.SessionResponse;
import com.sports.session_service.entity.Session;
import com.sports.session_service.exception.BadRequestException;
import com.sports.session_service.exception.ResourceNotFoundException;
import com.sports.session_service.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {

    private final SessionRepository sessionRepository;

    @Transactional
    public SessionResponse create(CreateSessionRequest request) {
        validateTimes(request.startTime(), request.endTime());

        Session session = Session.builder()
                .teamId(request.teamId())
                .title(request.title().trim())
                .sessionDate(request.sessionDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();

        return toResponse(sessionRepository.save(session));
    }

    public List<SessionResponse> getByTeam(Long teamId) {
        return sessionRepository.findByTeamIdOrderBySessionDateAscStartTimeAsc(teamId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SessionResponse update(Long id, UpdateSessionRequest request) {
        validateTimes(request.startTime(), request.endTime());

        Session session = getEntity(id);
        session.setTeamId(request.teamId());
        session.setTitle(request.title().trim());
        session.setSessionDate(request.sessionDate());
        session.setStartTime(request.startTime());
        session.setEndTime(request.endTime());

        return toResponse(sessionRepository.save(session));
    }

    @Transactional
    public void delete(Long id) {
        Session session = getEntity(id);
        sessionRepository.delete(session);
    }

    public Session getEntity(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + id));
    }

    private void validateTimes(LocalTime start, LocalTime end) {
        if (!end.isAfter(start)) {
            throw new BadRequestException("endTime must be after startTime");
        }
    }

    private SessionResponse toResponse(Session s) {
        return new SessionResponse(
                s.getId(),
                s.getTeamId(),
                s.getTitle(),
                s.getSessionDate(),
                s.getStartTime(),
                s.getEndTime(),
                s.getCreatedAt()
        );
    }
}
