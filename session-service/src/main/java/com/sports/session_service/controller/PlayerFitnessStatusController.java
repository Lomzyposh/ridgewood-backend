package com.sports.session_service.controller;

import com.sports.session_service.dto.request.PlayerFitnessStatusRequest;
import com.sports.session_service.dto.response.PlayerFitnessStatusResponse;
import com.sports.session_service.security.AuthenticatedUser;
import com.sports.session_service.service.PlayerFitnessStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fitness")
@RequiredArgsConstructor
public class PlayerFitnessStatusController {

    private final PlayerFitnessStatusService fitnessService;

    @GetMapping("/team/{teamId}")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<List<PlayerFitnessStatusResponse>> getTeamStatuses(
        @PathVariable Long teamId,
        Authentication authentication
    ) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.ok(
            fitnessService.getTeamStatuses(principal.userId(), teamId)
        );
    }

    @PutMapping("/student/{studentId}/team/{teamId}")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<PlayerFitnessStatusResponse> saveOrUpdate(
        @PathVariable Long studentId,
        @PathVariable Long teamId,
        @Valid @RequestBody PlayerFitnessStatusRequest request,
        Authentication authentication
    ) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.ok(
            fitnessService.saveOrUpdate(
                principal.userId(),
                studentId,
                teamId,
                request
            )
        );
    }

    @GetMapping("/me/team/{teamId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PlayerFitnessStatusResponse> getMyStatus(
        @PathVariable Long teamId,
        Authentication authentication
    ) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.ok(
            fitnessService.getStudentStatus(principal.userId(), teamId)
        );
    }
}
