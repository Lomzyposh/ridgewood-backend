package com.sports.session_service.controller;

import com.sports.session_service.dto.request.CreateSessionRequest;
import com.sports.session_service.dto.request.UpdateSessionRequest;
import com.sports.session_service.dto.response.SessionResponse;
import com.sports.session_service.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.create(request));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<SessionResponse>> getTeamSessions(@PathVariable Long teamId) {
        return ResponseEntity.ok(sessionService.getByTeam(teamId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionResponse> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSessionRequest request
    ) {
        return ResponseEntity.ok(sessionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelSession(@PathVariable Long id) {
        sessionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
