package com.peersphere.service.impl;

import com.peersphere.dto.request.CreateGroupRequest;
import com.peersphere.dto.request.UpdateGroupRequest;
import com.peersphere.dto.response.GroupMemberResponse;
import com.peersphere.dto.response.StudyGroupResponse;
import com.peersphere.dto.response.StudyGroupSummaryResponse;
import com.peersphere.entity.*;
import com.peersphere.exception.ResourceNotFoundException;
import com.peersphere.exception.UnauthorizedException;
import com.peersphere.repository.GroupMemberRepository;
import com.peersphere.repository.StudyGroupRepository;
import com.peersphere.repository.UserRepository;
import com.peersphere.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyGroupServiceImpl implements StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public StudyGroupResponse createGroup(String email, CreateGroupRequest request) {
        User creator = getUserByEmail(email);

        // Step 1 — Build and save the study group
        StudyGroup group = StudyGroup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .subject(request.getSubject())
                .maxMembers(request.getMaxMembers())
                .isActive(true)
                .createdBy(creator)
                .build();

        StudyGroup savedGroup = studyGroupRepository.save(group);

        // Step 2 — Automatically add the creator as ADMIN member
        GroupMember adminMember = GroupMember.builder()
                .studyGroup(savedGroup)
                .user(creator)
                .role(GroupRole.ADMIN)
                .build();

        groupMemberRepository.save(adminMember);

        return mapToFullResponse(savedGroup);
    }

    @Override
    public StudyGroupResponse getGroupById(Long groupId) {
        StudyGroup group = getGroupOrThrow(groupId);
        return mapToFullResponse(group);
    }

    @Override
    public List<StudyGroupSummaryResponse> getAllActiveGroups() {
        return studyGroupRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToSummaryResponse)
                .toList();
    }

    @Override
    public List<StudyGroupSummaryResponse> searchGroups(String keyword) {
        return studyGroupRepository.searchByNameOrSubject(keyword)
                .stream()
                .map(this::mapToSummaryResponse)
                .toList();
    }

    @Override
    public List<StudyGroupSummaryResponse> getMyGroups(String email) {
        User user = getUserByEmail(email);

        /**
         * We fetch all GroupMember rows for this user,
         * then extract the StudyGroup from each membership.
         * This gives us all groups the user is in —
         * whether they created them or just joined.
         */
        return groupMemberRepository.findByUserId(user.getId())
                .stream()
                .map(member -> mapToSummaryResponse(member.getStudyGroup()))
                .toList();
    }

    @Override
    @Transactional
    public StudyGroupResponse joinGroup(String email, Long groupId) {
        User user = getUserByEmail(email);
        StudyGroup group = getGroupOrThrow(groupId);

        // Check 1 — Is the group still active?
        if (!group.getIsActive()) {
            throw new IllegalArgumentException("This group is no longer active");
        }

        // Check 2 — Is the user already a member?
        if (groupMemberRepository.existsByStudyGroupIdAndUserId(groupId, user.getId())) {
            throw new IllegalArgumentException("You are already a member of this group");
        }

        // Check 3 — Is the group full?
        int currentCount = groupMemberRepository.countByStudyGroupId(groupId);
        if (currentCount >= group.getMaxMembers()) {
            throw new IllegalArgumentException("This group is full");
        }

        // All checks passed — create the membership
        GroupMember newMember = GroupMember.builder()
                .studyGroup(group)
                .user(user)
                .role(GroupRole.MEMBER)
                .build();

        groupMemberRepository.save(newMember);

        return mapToFullResponse(group);
    }

    @Override
    @Transactional
    public void leaveGroup(String email, Long groupId) {
        User user = getUserByEmail(email);
        getGroupOrThrow(groupId);

        GroupMember membership = groupMemberRepository
                .findByStudyGroupIdAndUserId(groupId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this group"));

        // Group admin cannot leave — they must delete the group instead
        if (membership.getRole() == GroupRole.ADMIN) {
            throw new IllegalArgumentException(
                    "As the group admin, you cannot leave. " +
                            "Please delete the group or transfer ownership."
            );
        }

        groupMemberRepository.delete(membership);
    }

    @Override
    @Transactional
    public StudyGroupResponse updateGroup(String email, Long groupId, UpdateGroupRequest request) {
        User user = getUserByEmail(email);
        StudyGroup group = getGroupOrThrow(groupId);

        // Only the ADMIN can update group details
        verifyAdminAccess(groupId, user.getId());

        if (request.getName() != null)       group.setName(request.getName());
        if (request.getDescription() != null) group.setDescription(request.getDescription());
        if (request.getSubject() != null)     group.setSubject(request.getSubject());
        if (request.getIsActive() != null)    group.setIsActive(request.getIsActive());

        // Cannot reduce max members below current member count
        if (request.getMaxMembers() != null) {
            int currentCount = groupMemberRepository.countByStudyGroupId(groupId);
            if (request.getMaxMembers() < currentCount) {
                throw new IllegalArgumentException(
                        "Cannot set max members below current member count of " + currentCount
                );
            }
            group.setMaxMembers(request.getMaxMembers());
        }

        return mapToFullResponse(studyGroupRepository.save(group));
    }

    @Override
    @Transactional
    public void deleteGroup(String email, Long groupId) {
        User user = getUserByEmail(email);
        StudyGroup group = getGroupOrThrow(groupId);

        // Only the original creator can delete the group
        if (!group.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException(
                    "Only the group creator can delete this group"
            );
        }

        // cascade = ALL on StudyGroup means all GroupMember rows
        // are deleted automatically when the group is deleted
        studyGroupRepository.delete(group);
    }

    @Override
    @Transactional
    public void removeMember(String requesterEmail, Long groupId, Long targetUserId) {
        User requester = getUserByEmail(requesterEmail);

        // Only an ADMIN can remove members
        verifyAdminAccess(groupId, requester.getId());

        // Cannot remove yourself with this endpoint — use leaveGroup instead
        if (requester.getId().equals(targetUserId)) {
            throw new IllegalArgumentException(
                    "Cannot remove yourself. Use the leave group endpoint."
            );
        }

        GroupMember targetMembership = groupMemberRepository
                .findByStudyGroupIdAndUserId(groupId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this group"));

        groupMemberRepository.delete(targetMembership);
    }

    // ── Private helper methods ─────────────────────────────────────

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private StudyGroup getGroupOrThrow(Long groupId) {
        return studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Study group not found with id: " + groupId));
    }

    /**
     * Checks if the given user is an ADMIN of the given group.
     * Throws UnauthorizedException if not — stops the operation immediately.
     */
    private void verifyAdminAccess(Long groupId, Long userId) {
        groupMemberRepository
                .findByStudyGroupIdAndUserIdAndRole(groupId, userId, GroupRole.ADMIN)
                .orElseThrow(() -> new UnauthorizedException(
                        "Only the group admin can perform this action"
                ));
    }

    private StudyGroupResponse mapToFullResponse(StudyGroup group) {
        List<GroupMemberResponse> memberResponses = group.getMembers()
                .stream()
                .map(m -> GroupMemberResponse.builder()
                        .userId(m.getUser().getId())
                        .fullName(m.getUser().getFullName())
                        .email(m.getUser().getEmail())
                        .department(m.getUser().getDepartment())
                        .profilePicture(m.getUser().getProfilePicture())
                        .role(m.getRole().name())
                        .joinedAt(m.getJoinedAt())
                        .build())
                .toList();

        return StudyGroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .subject(group.getSubject())
                .maxMembers(group.getMaxMembers())
                .currentMemberCount(memberResponses.size())
                .isActive(group.getIsActive())
                .createdByName(group.getCreatedBy().getFullName())
                .createdById(group.getCreatedBy().getId())
                .members(memberResponses)
                .createdAt(group.getCreatedAt())
                .build();
    }

    private StudyGroupSummaryResponse mapToSummaryResponse(StudyGroup group) {
        int count = groupMemberRepository.countByStudyGroupId(group.getId());
        return StudyGroupSummaryResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .subject(group.getSubject())
                .description(group.getDescription())
                .maxMembers(group.getMaxMembers())
                .currentMemberCount(count)
                .isActive(group.getIsActive())
                .createdByName(group.getCreatedBy().getFullName())
                .createdAt(group.getCreatedAt())
                .build();
    }
}