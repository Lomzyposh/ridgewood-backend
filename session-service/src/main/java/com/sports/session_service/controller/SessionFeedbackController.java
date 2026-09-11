package com.sports.session_service.controller;

import com.sports.session_service.dto.request.SessionFeedbackRequest;
import com.sports.session_service.dto.response.SessionFeedbackResponse;
import com.sports.session_service.security.AuthenticatedUser;
import com.sports.session_service.service.SessionFeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class SessionFeedbackController {

    private final SessionFeedbackService feedbackService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SessionFeedbackResponse> submitFeedback(
            @Valid @RequestBody SessionFeedbackRequest request,
            Authentication authentication
    ) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feedbackService.submit(principal.userId(), request));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('COACH','ADMIN') or #studentId == authentication.principal.userId()")
    public ResponseEntity<List<SessionFeedbackResponse>> getStudentFeedback(
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(feedbackService.getForStudent(studentId));
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('COACH','ADMIN')")
    public ResponseEntity<List<SessionFeedbackResponse>> getSessionFeedback(
            @PathVariable Long sessionId
    ) {
        return ResponseEntity.ok(feedbackService.getForSession(sessionId));
    }

    @GetMapping("/team/{teamId}")
    @PreAuthorize("hasAnyRole('COACH','ADMIN')")
    public ResponseEntity<List<SessionFeedbackResponse>> getTeamFeedback(
            @PathVariable Long teamId
    ) {
        return ResponseEntity.ok(feedbackService.getForTeam(teamId));
    }
}
