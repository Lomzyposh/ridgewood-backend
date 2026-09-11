package com.sports.session_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("attendanceAuth")
public class AttendanceAuthorizationService {

    public boolean canReadStudent(Long studentId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return false;
        }
        String role = user.role() == null ? "" : user.role().toUpperCase();
        return "COACH".equals(role)
                || "ADMIN".equals(role)
                || ("STUDENT".equals(role) && user.userId() != null && user.userId().equals(studentId));
    }
}
