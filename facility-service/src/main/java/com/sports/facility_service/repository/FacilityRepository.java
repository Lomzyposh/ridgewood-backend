package com.sports.facility_service.repository;

import com.sports.facility_service.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
    boolean existsByNameIgnoreCase(String name);
}
