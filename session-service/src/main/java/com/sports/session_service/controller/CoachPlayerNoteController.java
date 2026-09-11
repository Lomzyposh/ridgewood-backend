package com.sports.session_service.controller;

import com.sports.session_service.dto.request.CoachPlayerNoteRequest;
import com.sports.session_service.dto.response.CoachPlayerNoteResponse;
import com.sports.session_service.security.AuthenticatedUser;
import com.sports.session_service.service.CoachPlayerNoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/player-notes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COACH')")
public class CoachPlayerNoteController {

    private final CoachPlayerNoteService noteService;

    @PostMapping
    public ResponseEntity<CoachPlayerNoteResponse> create(
            @Valid @RequestBody CoachPlayerNoteRequest request,
            Authentication authentication
    ) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.create(principal.userId(), request));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<CoachPlayerNoteResponse>> getTeamNotes(
            @PathVariable Long teamId,
            Authentication authentication
    ) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.ok(noteService.getForTeam(principal.userId(), teamId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CoachPlayerNoteResponse>> getStudentNotes(
            @PathVariable Long studentId,
            Authentication authentication
    ) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.ok(noteService.getForStudent(principal.userId(), studentId));
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<CoachPlayerNoteResponse> update(
            @PathVariable Long noteId,
            @Valid @RequestBody CoachPlayerNoteRequest request,
            Authentication authentication
    ) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.ok(noteService.update(principal.userId(), noteId, request));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long noteId,
            Authentication authentication
    ) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        noteService.delete(principal.userId(), noteId);
        return ResponseEntity.noContent().build();
    }
}
