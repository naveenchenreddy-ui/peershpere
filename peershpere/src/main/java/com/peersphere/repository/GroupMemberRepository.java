package com.peersphere.repository;

import com.peersphere.entity.GroupMember;
import com.peersphere.entity.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    /**
     * Check if a user is already in a group.
     * Used before joining — prevents duplicate memberships.
     */
    boolean existsByStudyGroupIdAndUserId(Long groupId, Long userId);

    /**
     * Find a specific membership record.
     * Used when leaving a group or removing a member.
     */
    Optional<GroupMember> findByStudyGroupIdAndUserId(Long groupId, Long userId);

    /**
     * Find all groups a user has joined.
     * Used for "My Groups" on the dashboard.
     */
    List<GroupMember> findByUserId(Long userId);

    /**
     * Count members of a group.
     * Used to check if group is full before allowing new joins.
     */
    int countByStudyGroupId(Long groupId);

    /**
     * Find member by group, user and role.
     * Used to verify admin privileges.
     */
    Optional<GroupMember> findByStudyGroupIdAndUserIdAndRole(
            Long groupId, Long userId, GroupRole role);
    List<GroupMember> findByStudyGroupId(Long groupId);
}