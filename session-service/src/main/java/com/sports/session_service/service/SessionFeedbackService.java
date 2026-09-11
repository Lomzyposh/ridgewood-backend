package com.sports.session_service.service;

import com.sports.session_service.dto.request.SessionFeedbackRequest;
import com.sports.session_service.dto.response.SessionFeedbackResponse;
import com.sports.session_service.entity.Session;
import com.sports.session_service.entity.SessionFeedback;
import com.sports.session_service.exception.BadRequestException;
import com.sports.session_service.repository.SessionFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionFeedbackService {

    private final SessionFeedbackRepository feedbackRepository;
    private final SessionService sessionService;

    @Transactional
    public SessionFeedbackResponse submit(Long studentId, SessionFeedbackRequest request) {
        if (studentId == null) {
            throw new BadRequestException("Authenticated student id is missing from the token");
        }

        Session session = sessionService.getEntity(request.sessionId());
        ensureSessionCompleted(session);

        SessionFeedback feedback = feedbackRepository
                .findBySessionIdAndStudentId(session.getId(), studentId)
                .orElseGet(() -> SessionFeedback.builder()
                        .session(session)
                        .studentId(studentId)
                        .build());

        feedback.setRating(request.rating());
        feedback.setMood(request.mood().trim());
        feedback.setComment(request.comment().trim());

        return toResponse(feedbackRepository.save(feedback));
    }

    public List<SessionFeedbackResponse> getForStudent(Long studentId) {
        return feedbackRepository.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<SessionFeedbackResponse> getForSession(Long sessionId) {
        sessionService.getEntity(sessionId);
        return feedbackRepository.findBySessionIdOrderByCreatedAtDesc(sessionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<SessionFeedbackResponse> getForTeam(Long teamId) {
        return feedbackRepository.findBySessionTeamIdOrderByCreatedAtDesc(teamId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void ensureSessionCompleted(Session session) {
        LocalDate today = LocalDate.now();
        if (session.getSessionDate().isAfter(today)) {
            throw new BadRequestException("Feedback can only be submitted after the session has finished");
        }

        if (session.getSessionDate().isEqual(today) && session.getEndTime().isAfter(LocalTime.now())) {
            throw new BadRequestException("Feedback can only be submitted after the session has finished");
        }
    }

    private SessionFeedbackResponse toResponse(SessionFeedback feedback) {
        Session session = feedback.getSession();
        return new SessionFeedbackResponse(
                feedback.getId(),
                session.getId(),
                session.getTeamId(),
                session.getTitle(),
                session.getSessionDate(),
                feedback.getStudentId(),
                feedback.getRating(),
                feedback.getMood(),
                feedback.getComment(),
                feedback.getCreatedAt(),
                feedback.getUpdatedAt()
        );
    }
}
