package com.sports.facility_service.repository;

import com.sports.facility_service.entity.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.*;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = "facility")
    List<Booking> findByFacilityIdOrderByDateAscStartTimeAsc(Long facilityId);

    @EntityGraph(attributePaths = "facility")
    List<Booking> findByTeamIdOrderByDateAscStartTimeAsc(Long teamId);

    @Query("""
            select count(b) > 0
            from Booking b
            where b.facility.id = :facilityId
              and b.date = :date
              and b.startTime < :requestedEnd
              and b.endTime > :requestedStart
            """)
    boolean existsOverlappingBooking(
            @Param("facilityId") Long facilityId,
            @Param("date") LocalDate date,
            @Param("requestedStart") LocalTime requestedStart,
            @Param("requestedEnd") LocalTime requestedEnd
    );
}
