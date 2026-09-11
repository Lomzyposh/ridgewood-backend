package com.sports.session_service.controller;

import com.sports.session_service.dto.request.BulkAttendanceRequest;
import com.sports.session_service.dto.response.AttendanceResponse;
import com.sports.session_service.dto.response.TeamAttendanceRateResponse;
import com.sports.session_service.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<List<AttendanceResponse>> submitAttendance(
            @Valid @RequestBody BulkAttendanceRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.submitBulk(request));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<AttendanceResponse>> getSessionAttendance(@PathVariable Long sessionId) {
        return ResponseEntity.ok(attendanceService.getForSession(sessionId));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("@attendanceAuth.canReadStudent(#studentId, authentication)")
    public ResponseEntity<List<AttendanceResponse>> getStudentAttendance(
            @PathVariable Long studentId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(attendanceService.getForStudent(studentId));
    }

    @GetMapping("/team/{teamId}/rate")
    public ResponseEntity<TeamAttendanceRateResponse> getTeamAttendanceRate(@PathVariable Long teamId) {
        return ResponseEntity.ok(attendanceService.getTeamRate(teamId));
    }
}
