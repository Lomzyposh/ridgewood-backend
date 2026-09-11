package com.sports.team_service.controller;

import com.sports.team_service.dto.response.JoinRequestResponse;
import com.sports.team_service.service.JoinRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/join-requests")
@RequiredArgsConstructor
public class JoinRequestController {

    private final JoinRequestService joinRequestService;

    @PutMapping("/{id}/approve")
    @PreAuthorize("isAuthenticated()")
    public JoinRequestResponse approveJoinRequest(@PathVariable Long id) {
        return joinRequestService.approveJoinRequest(id);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("isAuthenticated()")
    public JoinRequestResponse rejectJoinRequest(@PathVariable Long id) {
        return joinRequestService.rejectJoinRequest(id);
    }

    @GetMapping("/student/{studentId}")
    public List<JoinRequestResponse> getStudentRequests(@PathVariable Long studentId) {
        return joinRequestService.getByStudent(studentId);
    }

    @GetMapping("/coach/{coachId}")
    public List<JoinRequestResponse> getCoachRequests(@PathVariable Long coachId) {
        return joinRequestService.getByCoach(coachId);
    }
}
