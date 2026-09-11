package com.sports.facility_service.controller;

import com.sports.facility_service.dto.request.FacilityRequest;
import com.sports.facility_service.dto.response.BookingResponse;
import com.sports.facility_service.dto.response.FacilityResponse;
import com.sports.facility_service.service.BookingService;
import com.sports.facility_service.service.FacilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;
    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<FacilityResponse>> getAll() {
        return ResponseEntity.ok(facilityService.getAllFacilities());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacilityResponse> create(@Valid @RequestBody FacilityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facilityService.createFacility(request));
    }

    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookings(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getFacilityBookings(id));
    }
}
