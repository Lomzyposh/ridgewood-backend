package com.sports.team_service.service;

import com.sports.team_service.dto.request.JoinRequestCreateRequest;
import com.sports.team_service.dto.response.JoinRequestResponse;

import java.util.List;

public interface JoinRequestService {

    JoinRequestResponse requestToJoin(Long teamId, JoinRequestCreateRequest request);

    JoinRequestResponse approveJoinRequest(Long id);

    JoinRequestResponse rejectJoinRequest(Long id);
    List<JoinRequestResponse> getByTeam(Long teamId);
    List<JoinRequestResponse> getByStudent(Long studentId);
    List<JoinRequestResponse> getByCoach(Long coachId);
}
