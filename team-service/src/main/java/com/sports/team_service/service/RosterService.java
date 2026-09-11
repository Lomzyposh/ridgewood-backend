package com.sports.team_service.service;

import com.sports.team_service.dto.request.UpdateRosterRoleRequest;
import com.sports.team_service.dto.response.RosterMemberResponse;

import java.util.List;

public interface RosterService {

    List<RosterMemberResponse> getActiveRoster(Long teamId);

    RosterMemberResponse updateRole(Long rosterMemberId, UpdateRosterRoleRequest request);

    void removeFromRoster(Long rosterMemberId);
}
