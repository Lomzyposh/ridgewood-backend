package com.sports.session_service.repository;

import com.sports.session_service.entity.Attendance;
import com.sports.session_service.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findBySessionIdOrderByDateDescStudentIdAsc(Long sessionId);
    List<Attendance> findByStudentIdOrderByDateDesc(Long studentId);
    Optional<Attendance> findBySessionIdAndStudentIdAndDate(Long sessionId, Long studentId, LocalDate date);
    long countBySessionTeamId(Long teamId);
    long countBySessionTeamIdAndStatus(Long teamId, AttendanceStatus status);
}
