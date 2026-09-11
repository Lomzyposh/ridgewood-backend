package com.sports.facility_service.service;

import com.sports.facility_service.dto.request.FacilityRequest;
import com.sports.facility_service.dto.response.FacilityResponse;
import com.sports.facility_service.entity.Facility;
import com.sports.facility_service.exception.BadRequestException;
import com.sports.facility_service.exception.ResourceNotFoundException;
import com.sports.facility_service.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public List<FacilityResponse> getAllFacilities() {
        return facilityRepository.findAll().stream()
                .map(this::map)
                .toList();
    }

    public FacilityResponse createFacility(FacilityRequest request) {
        if (facilityRepository.existsByNameIgnoreCase(request.name().trim())) {
            throw new BadRequestException("A facility with this name already exists");
        }

        Facility facility = Facility.builder()
                .name(request.name().trim())
                .type(request.type().trim())
                .build();

        return map(facilityRepository.save(facility));
    }

    public Facility getEntity(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found: " + id));
    }

    private FacilityResponse map(Facility facility) {
        return new FacilityResponse(facility.getId(), facility.getName(), facility.getType());
    }
}
