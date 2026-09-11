package com.sports.facility_service.service;

import com.sports.facility_service.dto.request.BookingRequest;
import com.sports.facility_service.dto.response.BookingResponse;
import com.sports.facility_service.entity.Booking;
import com.sports.facility_service.entity.Facility;
import com.sports.facility_service.exception.BadRequestException;
import com.sports.facility_service.exception.BookingConflictException;
import com.sports.facility_service.exception.ResourceNotFoundException;
import com.sports.facility_service.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FacilityService facilityService;

    public List<BookingResponse> getFacilityBookings(Long facilityId) {
        facilityService.getEntity(facilityId);
        return bookingRepository.findByFacilityIdOrderByDateAscStartTimeAsc(facilityId)
                .stream()
                .map(this::map)
                .toList();
    }

    public List<BookingResponse> getTeamBookings(Long teamId) {
        return bookingRepository.findByTeamIdOrderByDateAscStartTimeAsc(teamId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        if (!request.startTime().isBefore(request.endTime())) {
            throw new BadRequestException("startTime must be earlier than endTime");
        }

        Facility facility = facilityService.getEntity(request.facilityId());

        boolean overlaps = bookingRepository.existsOverlappingBooking(
                request.facilityId(),
                request.date(),
                request.startTime(),
                request.endTime()
        );

        if (overlaps) {
            throw new BookingConflictException(
                    "Facility is already booked for an overlapping time range on " + request.date()
            );
        }

        Booking booking = Booking.builder()
                .facility(facility)
                .teamId(request.teamId())
                .day(request.date().getDayOfWeek())
                .date(request.date())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();

        return map(bookingRepository.save(booking));
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        bookingRepository.delete(booking);
    }

    private BookingResponse map(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getFacility().getId(),
                booking.getFacility().getName(),
                booking.getTeamId(),
                booking.getDay(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getDate(),
                booking.getCreatedAt()
        );
    }
}
