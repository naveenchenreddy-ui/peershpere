package com.peersphere.service.impl;

import com.peersphere.dto.response.PeerRecommendationResponse;
import com.peersphere.entity.GroupMember;
import com.peersphere.entity.User;
import com.peersphere.exception.ResourceNotFoundException;
import com.peersphere.repository.GroupMemberRepository;
import com.peersphere.repository.UserRepository;
import com.peersphere.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    // Scoring weights — adjust these to tune recommendation quality
    private static final int WEIGHT_SHARED_GROUP      = 5;
    private static final int WEIGHT_SAME_DEPARTMENT   = 4;
    private static final int WEIGHT_SHARED_INTEREST   = 3;
    private static final int WEIGHT_SAME_SEMESTER     = 2;
    private static final int WEIGHT_SHARED_SKILL      = 2;

    // Maximum number of recommendations to return
    private static final int MAX_RECOMMENDATIONS = 10;

    @Override
    public List<PeerRecommendationResponse> getRecommendedPeers(String email) {
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Step 1 — Get IDs of groups the current user belongs to
        Set<Long> currentUserGroupIds = groupMemberRepository
                .findByUserId(currentUser.getId())
                .stream()
                .map(gm -> gm.getStudyGroup().getId())
                .collect(Collectors.toSet());

        // Step 2 — Normalize current user's data for comparison
        Set<String> myInterests = normalizeToSet(currentUser.getInterests());
        Set<String> mySkills    = normalizeToSet(currentUser.getSkills());

        // Step 3 — Load all other users and score them
        List<PeerRecommendationResponse> scored = userRepository
                .findAllExcept(currentUser.getId())
                .stream()
                .map(candidate -> scoreCandidate(
                        currentUser, candidate,
                        myInterests, mySkills, currentUserGroupIds))
                .filter(r -> r.getCompatibilityScore() > 0) // exclude zero-match users
                .sorted(Comparator.comparingInt(
                        PeerRecommendationResponse::getCompatibilityScore).reversed())
                .limit(MAX_RECOMMENDATIONS)
                .toList();

        return scored;
    }

    /**
     * Calculates the compatibility score between the current user
     * and one candidate user.
     *
     * Returns a fully built PeerRecommendationResponse including:
     * - The score
     * - What they have in common
     * - A human-readable match reason
     */
    private PeerRecommendationResponse scoreCandidate(
            User currentUser,
            User candidate,
            Set<String> myInterests,
            Set<String> mySkills,
            Set<Long> myGroupIds) {

        int score = 0;
        List<String> reasons = new ArrayList<>();

        // ── Get candidate's group IDs ──────────────────────────────
        Set<Long> candidateGroupIds = groupMemberRepository
                .findByUserId(candidate.getId())
                .stream()
                .map(gm -> gm.getStudyGroup().getId())
                .collect(Collectors.toSet());

        // ── Shared Groups ──────────────────────────────────────────
        Set<Long> sharedGroupIds = new HashSet<>(myGroupIds);
        sharedGroupIds.retainAll(candidateGroupIds);
        boolean sameGroup = !sharedGroupIds.isEmpty();

        if (sameGroup) {
            score += WEIGHT_SHARED_GROUP * sharedGroupIds.size();
            reasons.add("in " + sharedGroupIds.size() + " shared group(s)");
        }

        // ── Same Department ────────────────────────────────────────
        boolean sameDepartment = currentUser.getDepartment() != null
                && currentUser.getDepartment().equalsIgnoreCase(
                candidate.getDepartment());

        if (sameDepartment) {
            score += WEIGHT_SAME_DEPARTMENT;
            reasons.add("same department");
        }

        // ── Same Semester ──────────────────────────────────────────
        boolean sameSemester = currentUser.getSemester() != null
                && currentUser.getSemester().equalsIgnoreCase(
                candidate.getSemester());

        if (sameSemester) {
            score += WEIGHT_SAME_SEMESTER;
            reasons.add("same semester");
        }

        // ── Shared Interests ───────────────────────────────────────
        Set<String> candidateInterests = normalizeToSet(candidate.getInterests());
        List<String> commonInterests = myInterests.stream()
                .filter(i -> candidateInterests.stream()
                        .anyMatch(ci -> ci.equalsIgnoreCase(i)))
                .collect(Collectors.toList());

        if (!commonInterests.isEmpty()) {
            score += WEIGHT_SHARED_INTEREST * commonInterests.size();
            reasons.add(commonInterests.size() + " shared interest(s)");
        }

        // ── Shared Skills ──────────────────────────────────────────
        Set<String> candidateSkills = normalizeToSet(candidate.getSkills());
        List<String> commonSkills = mySkills.stream()
                .filter(s -> candidateSkills.stream()
                        .anyMatch(cs -> cs.equalsIgnoreCase(s)))
                .collect(Collectors.toList());

        if (!commonSkills.isEmpty()) {
            score += WEIGHT_SHARED_SKILL * commonSkills.size();
            reasons.add(commonSkills.size() + " shared skill(s)");
        }

        // ── Build human-readable match reason ──────────────────────
        String matchReason = reasons.isEmpty()
                ? "Potential study partner"
                : String.join(", ", reasons);

        return PeerRecommendationResponse.builder()
                .userId(candidate.getId())
                .fullName(candidate.getFullName())
                .email(candidate.getEmail())
                .department(candidate.getDepartment())
                .semester(candidate.getSemester())
                .profilePicture(candidate.getProfilePicture())
                .skills(candidate.getSkills())
                .interests(candidate.getInterests())
                .commonInterests(commonInterests)
                .commonSkills(commonSkills)
                .sameGroup(sameGroup)
                .sameDepartment(sameDepartment)
                .compatibilityScore(score)
                .matchReason(matchReason)
                .build();
    }

    /**
     * Converts a List<String> to a Set<String> safely.
     * Returns empty set if the list is null (user hasn't added any yet).
     * Set gives O(1) lookup when checking for intersections.
     */
    private Set<String> normalizeToSet(List<String> list) {
        if (list == null) return Collections.emptySet();
        return new HashSet<>(list);
    }
}