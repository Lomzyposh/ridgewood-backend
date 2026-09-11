package com.sports.facility_service.controller;

import com.sports.facility_service.dto.request.BookingRequest;
import com.sports.facility_service.dto.response.BookingResponse;
import com.sports.facility_service.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/team/{teamId}")
    @PreAuthorize("hasAnyRole('COACH','ADMIN','STUDENT')")
    public ResponseEntity<List<BookingResponse>> getTeamBookings(@PathVariable Long teamId) {
        return ResponseEntity.ok(bookingService.getTeamBookings(teamId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('COACH','ADMIN')")
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COACH','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
