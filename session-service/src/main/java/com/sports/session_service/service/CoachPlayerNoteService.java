package com.sports.session_service.service;

import com.sports.session_service.dto.request.CoachPlayerNoteRequest;
import com.sports.session_service.dto.response.CoachPlayerNoteResponse;
import com.sports.session_service.entity.CoachPlayerNote;
import com.sports.session_service.entity.Session;
import com.sports.session_service.exception.BadRequestException;
import com.sports.session_service.exception.ResourceNotFoundException;
import com.sports.session_service.repository.CoachPlayerNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoachPlayerNoteService {

    private final CoachPlayerNoteRepository noteRepository;
    private final SessionService sessionService;

    @Transactional
    public CoachPlayerNoteResponse create(Long coachId, CoachPlayerNoteRequest request) {
        if (coachId == null) {
            throw new BadRequestException("Authenticated coach id is missing from the token");
        }

        Session session = resolveSession(request.sessionId(), request.teamId());

        CoachPlayerNote note = CoachPlayerNote.builder()
                .coachId(coachId)
                .studentId(request.studentId())
                .teamId(request.teamId())
                .session(session)
                .focus(request.focus().trim())
                .note(request.note().trim())
                .build();

        return toResponse(noteRepository.save(note));
    }

    public List<CoachPlayerNoteResponse> getForTeam(Long coachId, Long teamId) {
        return noteRepository.findByCoachIdAndTeamIdOrderByCreatedAtDesc(coachId, teamId)
                .stream().map(this::toResponse).toList();
    }

    public List<CoachPlayerNoteResponse> getForStudent(Long coachId, Long studentId) {
        return noteRepository.findByCoachIdAndStudentIdOrderByCreatedAtDesc(coachId, studentId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public CoachPlayerNoteResponse update(Long coachId, Long noteId, CoachPlayerNoteRequest request) {
        CoachPlayerNote note = findOwned(noteId, coachId);
        Session session = resolveSession(request.sessionId(), request.teamId());

        note.setStudentId(request.studentId());
        note.setTeamId(request.teamId());
        note.setSession(session);
        note.setFocus(request.focus().trim());
        note.setNote(request.note().trim());
        return toResponse(noteRepository.save(note));
    }

    @Transactional
    public void delete(Long coachId, Long noteId) {
        noteRepository.delete(findOwned(noteId, coachId));
    }

    private CoachPlayerNote findOwned(Long noteId, Long coachId) {
        return noteRepository.findByIdAndCoachId(noteId, coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Player note not found"));
    }

    private Session resolveSession(Long sessionId, Long teamId) {
        if (sessionId == null) return null;
        Session session = sessionService.getEntity(sessionId);
        if (!session.getTeamId().equals(teamId)) {
            throw new BadRequestException("The selected session does not belong to this team");
        }
        return session;
    }

    private CoachPlayerNoteResponse toResponse(CoachPlayerNote note) {
        Session session = note.getSession();
        return new CoachPlayerNoteResponse(
                note.getId(), note.getCoachId(), note.getStudentId(), note.getTeamId(),
                session == null ? null : session.getId(),
                session == null ? null : session.getTitle(),
                session == null ? null : session.getSessionDate(),
                note.getFocus(), note.getNote(), note.getCreatedAt(), note.getUpdatedAt()
        );
    }
}
