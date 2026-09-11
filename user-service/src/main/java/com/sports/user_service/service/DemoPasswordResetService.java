package com.sports.user_service.service;

import com.sports.user_service.dto.response.DemoFaceVerifyResponse;
import com.sports.user_service.dto.response.DemoResetIdentifyResponse;
import com.sports.user_service.dto.response.DemoResetPasswordResponse;
import com.sports.user_service.entity.User;
import com.sports.user_service.exception.BadRequestException;
import com.sports.user_service.exception.ResourceNotFoundException;
import com.sports.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class DemoPasswordResetService {

    private static final long RESET_SESSION_MINUTES = 10;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
     * DEMO ONLY:
     * reset sessions live in memory and disappear when user-service restarts.
     * No face image/biometric data is stored.
     */
    private final Map<String, ResetSession> resetSessions = new ConcurrentHashMap<>();

    public DemoResetIdentifyResponse identify(Long accountId) {
        cleanupExpired();

        User user = userRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("No Ridgewood account was found with that ID"));

        String token = UUID.randomUUID().toString();
        resetSessions.put(
                token,
                new ResetSession(user.getId(), false, Instant.now().plus(RESET_SESSION_MINUTES, ChronoUnit.MINUTES))
        );

        return new DemoResetIdentifyResponse(
                token,
                user.getId(),
                user.getFullName(),
                maskEmail(user.getEmail()),
                user.getRole().name(),
                "Account found. Continue to demo face verification."
        );
    }

    public DemoFaceVerifyResponse verifyFace(String resetSession) {
        ResetSession session = requireSession(resetSession);

        /*
         * PRESENTATION / DEMO MODE ONLY.
         * The frontend merely proves that a camera frame was captured.
         * This endpoint intentionally accepts that capture and DOES NOT perform
         * facial recognition or biometric identity matching.
         */
        resetSessions.put(
                resetSession,
                new ResetSession(session.userId(), true, session.expiresAt())
        );

        return new DemoFaceVerifyResponse(
                true,
                "Demo face check accepted. You can now choose a new password."
        );
    }

    @Transactional
    public DemoResetPasswordResponse reset(
            String resetSession,
            String newPassword,
            String confirmPassword
    ) {
        ResetSession session = requireSession(resetSession);

        if (!session.faceVerified()) {
            throw new BadRequestException("Complete face verification before resetting the password");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("Passwords do not match");
        }

        if (newPassword.length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters");
        }

        User user = userRepository.findById(session.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User account not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // One-time reset session.
        resetSessions.remove(resetSession);

        return new DemoResetPasswordResponse(
                true,
                "Password reset successful. You can now sign in with your new password."
        );
    }

    private ResetSession requireSession(String resetSession) {
        cleanupExpired();

        ResetSession session = resetSessions.get(resetSession);
        if (session == null || session.expiresAt().isBefore(Instant.now())) {
            if (resetSession != null) {
                resetSessions.remove(resetSession);
            }
            throw new BadRequestException("This password reset session has expired. Start again.");
        }

        return session;
    }

    private void cleanupExpired() {
        Instant now = Instant.now();
        resetSessions.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "Hidden email";

        String[] parts = email.split("@", 2);
        String local = parts[0];
        String domain = parts[1];

        String visible = local.isBlank() ? "*" : local.substring(0, 1);
        return visible + "***@" + domain;
    }

    private record ResetSession(
            Long userId,
            boolean faceVerified,
            Instant expiresAt
    ) {}
}
