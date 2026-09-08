package com.peersphere.service;

import com.peersphere.dto.request.CreateGroupRequest;
import com.peersphere.dto.request.UpdateGroupRequest;
import com.peersphere.dto.response.StudyGroupResponse;
import com.peersphere.dto.response.StudyGroupSummaryResponse;

import java.util.List;

public interface StudyGroupService {

    StudyGroupResponse createGroup(String email, CreateGroupRequest request);

    StudyGroupResponse getGroupById(Long groupId);

    List<StudyGroupSummaryResponse> getAllActiveGroups();

    List<StudyGroupSummaryResponse> searchGroups(String keyword);

    List<StudyGroupSummaryResponse> getMyGroups(String email);

    StudyGroupResponse joinGroup(String email, Long groupId);

    void leaveGroup(String email, Long groupId);

    StudyGroupResponse updateGroup(String email, Long groupId, UpdateGroupRequest request);

    void deleteGroup(String email, Long groupId);

    void removeMember(String requesterEmail, Long groupId, Long targetUserId);
}