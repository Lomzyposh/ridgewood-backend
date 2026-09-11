package com.sports.session_service.service;

import com.sports.session_service.dto.request.CreateSessionRequest;
import com.sports.session_service.dto.request.UpdateSessionRequest;
import com.sports.session_service.dto.response.SessionResponse;
import com.sports.session_service.entity.Session;
import com.sports.session_service.exception.BadRequestException;
import com.sports.session_service.exception.ResourceNotFoundException;
import com.sports.session_service.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService(sessionRepository);
    }

    @Test
    void createSessionSavesValidTrainingSession() {
        CreateSessionRequest request = new CreateSessionRequest(
                1L, "  Tactical Training  ", LocalDate.of(2026, 9, 12),
                LocalTime.of(10, 0), LocalTime.of(12, 0));

        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> {
            Session session = invocation.getArgument(0);
            session.setId(10L);
            return session;
        });

        SessionResponse response = sessionService.create(request);

        assertEquals(10L, response.id());
        assertEquals(1L, response.teamId());
        assertEquals("Tactical Training", response.title());
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void createSessionRejectsEndTimeBeforeStartTime() {
        CreateSessionRequest request = new CreateSessionRequest(
                1L, "Training", LocalDate.of(2026, 9, 12),
                LocalTime.of(12, 0), LocalTime.of(10, 0));

        assertThrows(BadRequestException.class, () -> sessionService.create(request));
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void updateSessionChangesExistingSession() {
        Session existing = Session.builder()
                .id(5L).teamId(1L).title("Old Training")
                .sessionDate(LocalDate.of(2026, 9, 12))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .build();

        UpdateSessionRequest request = new UpdateSessionRequest(
                2L, "Updated Training", LocalDate.of(2026, 9, 13),
                LocalTime.of(14, 0), LocalTime.of(16, 0));

        when(sessionRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(sessionRepository.save(existing)).thenReturn(existing);

        SessionResponse response = sessionService.update(5L, request);

        assertEquals(2L, response.teamId());
        assertEquals("Updated Training", response.title());
        assertEquals(LocalTime.of(14, 0), response.startTime());
        verify(sessionRepository).save(existing);
    }

    @Test
    void deleteSessionDeletesExistingSession() {
        Session existing = Session.builder().id(5L).teamId(1L).title("Training").build();
        when(sessionRepository.findById(5L)).thenReturn(Optional.of(existing));

        sessionService.delete(5L);

        verify(sessionRepository).delete(existing);
    }

    @Test
    void getEntityRejectsUnknownSession() {
        when(sessionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sessionService.getEntity(999L));
    }
}
