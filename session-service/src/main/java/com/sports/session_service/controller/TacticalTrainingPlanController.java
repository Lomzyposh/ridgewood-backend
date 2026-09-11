package com.sports.session_service.controller;

import com.sports.session_service.dto.request.SaveTacticalTrainingPlanRequest;
import com.sports.session_service.dto.response.TacticalTrainingPlanResponse;
import com.sports.session_service.security.AuthenticatedUser;
import com.sports.session_service.service.TacticalTrainingPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tactical-training-plans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('COACH')")
public class TacticalTrainingPlanController {

    private final TacticalTrainingPlanService service;

    @PutMapping("/session/{sessionId}")
    public ResponseEntity<List<TacticalTrainingPlanResponse>> saveSessionPlan(
            @PathVariable Long sessionId,
            @Valid @RequestBody SaveTacticalTrainingPlanRequest request,
            Authentication authentication
    ) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();

        return ResponseEntity.ok(
                service.replaceSessionPlan(principal.userId(), sessionId, request)
        );
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<TacticalTrainingPlanResponse>> getSessionPlan(
            @PathVariable Long sessionId,
            Authentication authentication
    ) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();

        return ResponseEntity.ok(
                service.getForSession(principal.userId(), sessionId)
        );
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TacticalTrainingPlanResponse>> getTeamPlans(
            @PathVariable Long teamId,
            Authentication authentication
    ) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();

        return ResponseEntity.ok(
                service.getForTeam(principal.userId(), teamId)
        );
    }

    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> clearSessionPlan(
            @PathVariable Long sessionId,
            Authentication authentication
    ) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        service.clearSessionPlan(principal.userId(), sessionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOne(
            @PathVariable Long id,
            Authentication authentication
    ) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        service.deleteOne(principal.userId(), id);
        return ResponseEntity.noContent().build();
    }
}
