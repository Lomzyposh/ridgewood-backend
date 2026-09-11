package com.sports.facility_service.service;

import com.sports.facility_service.dto.request.BookingRequest;
import com.sports.facility_service.dto.response.BookingResponse;
import com.sports.facility_service.entity.Booking;
import com.sports.facility_service.entity.Facility;
import com.sports.facility_service.exception.BadRequestException;
import com.sports.facility_service.exception.BookingConflictException;
import com.sports.facility_service.exception.ResourceNotFoundException;
import com.sports.facility_service.repository.BookingRepository;
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
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FacilityService facilityService;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, facilityService);
    }

    @Test
    void createBookingSavesValidBooking() {
        Facility facility = Facility.builder()
                .id(1L)
                .name("Main Sports Hall")
                .type("Indoor")
                .build();

        BookingRequest request = new BookingRequest(
                1L,
                5L,
                LocalDate.of(2026, 9, 12),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0)
        );

        when(facilityService.getEntity(1L)).thenReturn(facility);
        when(bookingRepository.existsOverlappingBooking(
                1L,
                request.date(),
                request.startTime(),
                request.endTime()
        )).thenReturn(false);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(10L);
            return booking;
        });

        BookingResponse response = bookingService.createBooking(request);

        assertEquals(10L, response.id());
        assertEquals(1L, response.facilityId());
        assertEquals("Main Sports Hall", response.facilityName());
        assertEquals(5L, response.teamId());

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBookingRejectsOverlappingTimeSlot() {
        Facility facility = Facility.builder()
                .id(1L)
                .name("Main Sports Hall")
                .type("Indoor")
                .build();

        BookingRequest request = new BookingRequest(
                1L,
                5L,
                LocalDate.of(2026, 9, 12),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0)
        );

        when(facilityService.getEntity(1L)).thenReturn(facility);
        when(bookingRepository.existsOverlappingBooking(
                1L,
                request.date(),
                request.startTime(),
                request.endTime()
        )).thenReturn(true);

        assertThrows(
                BookingConflictException.class,
                () -> bookingService.createBooking(request)
        );

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingRejectsInvalidTimeRange() {
        BookingRequest request = new BookingRequest(
                1L,
                5L,
                LocalDate.of(2026, 9, 12),
                LocalTime.of(12, 0),
                LocalTime.of(10, 0)
        );

        assertThrows(
                BadRequestException.class,
                () -> bookingService.createBooking(request)
        );

        verifyNoInteractions(facilityService);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void deleteBookingDeletesExistingBooking() {
        Facility facility = Facility.builder()
                .id(1L)
                .name("Main Sports Hall")
                .type("Indoor")
                .build();

        Booking booking = Booking.builder()
                .id(10L)
                .facility(facility)
                .teamId(5L)
                .build();

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(10L);

        verify(bookingRepository).delete(booking);
    }

    @Test
    void deleteBookingRejectsUnknownBooking() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookingService.deleteBooking(999L)
        );

        verify(bookingRepository, never()).delete(any(Booking.class));
    }
}
