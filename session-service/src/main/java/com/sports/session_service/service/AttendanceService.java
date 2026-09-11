package com.sports.session_service.service;

import com.sports.session_service.dto.request.AttendanceEntryRequest;
import com.sports.session_service.dto.request.BulkAttendanceRequest;
import com.sports.session_service.dto.response.AttendanceResponse;
import com.sports.session_service.dto.response.TeamAttendanceRateResponse;
import com.sports.session_service.entity.Attendance;
import com.sports.session_service.entity.AttendanceStatus;
import com.sports.session_service.entity.Session;
import com.sports.session_service.exception.BadRequestException;
import com.sports.session_service.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SessionService sessionService;

    @Transactional
    public List<AttendanceResponse> submitBulk(BulkAttendanceRequest request) {
        Session session = sessionService.getEntity(request.sessionId());
        LocalDate attendanceDate = request.date() == null ? LocalDate.now() : request.date();

        Set<Long> submittedStudentIds = new HashSet<>();
        for (AttendanceEntryRequest entry : request.attendance()) {
            if (!submittedStudentIds.add(entry.studentId())) {
                throw new BadRequestException("Duplicate studentId in attendance payload: " + entry.studentId());
            }
        }

        return request.attendance().stream()
                .map(entry -> upsert(session, attendanceDate, entry))
                .map(this::toResponse)
                .toList();
    }

    public List<AttendanceResponse> getForSession(Long sessionId) {
        sessionService.getEntity(sessionId);
        return attendanceRepository.findBySessionIdOrderByDateDescStudentIdAsc(sessionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AttendanceResponse> getForStudent(Long studentId) {
        return attendanceRepository.findByStudentIdOrderByDateDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TeamAttendanceRateResponse getTeamRate(Long teamId) {
        long total = attendanceRepository.countBySessionTeamId(teamId);
        long present = attendanceRepository.countBySessionTeamIdAndStatus(teamId, AttendanceStatus.PRESENT);
        double rate = total == 0 ? 0.0 : Math.round((present * 10000.0 / total)) / 100.0;
        return new TeamAttendanceRateResponse(teamId, total, present, rate);
    }

    private Attendance upsert(Session session, LocalDate date, AttendanceEntryRequest entry) {
        Attendance attendance = attendanceRepository
                .findBySessionIdAndStudentIdAndDate(session.getId(), entry.studentId(), date)
                .orElseGet(() -> Attendance.builder()
                        .session(session)
                        .studentId(entry.studentId())
                        .date(date)
                        .build());

        attendance.setStatus(entry.status());
        return attendanceRepository.save(attendance);
    }

    private AttendanceResponse toResponse(Attendance a) {
        return new AttendanceResponse(
                a.getId(),
                a.getSession().getId(),
                a.getSession().getTeamId(),
                a.getStudentId(),
                a.getDate(),
                a.getStatus()
        );
    }
}
