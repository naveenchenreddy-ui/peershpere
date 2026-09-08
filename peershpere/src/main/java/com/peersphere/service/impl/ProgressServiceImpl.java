package com.peersphere.service.impl;

import com.peersphere.dto.request.LogProgressRequest;
import com.peersphere.dto.response.*;
import com.peersphere.entity.ProgressEntry;
import com.peersphere.entity.StudyGroup;
import com.peersphere.entity.User;
import com.peersphere.exception.ResourceNotFoundException;
import com.peersphere.exception.UnauthorizedException;
import com.peersphere.repository.ProgressRepository;
import com.peersphere.repository.StudyGroupRepository;
import com.peersphere.repository.UserRepository;
import com.peersphere.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;

    @Override
    @Transactional
    public ProgressEntryResponse logProgress(String email, LogProgressRequest request) {
        User user = getUserByEmail(email);

        StudyGroup group = null;
        if (request.getGroupId() != null) {
            group = studyGroupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        }

        ProgressEntry entry = ProgressEntry.builder()
                .topicName(request.getTopicName())
                .description(request.getDescription())
                .studyMinutes(request.getStudyMinutes())
                .studyDate(request.getStudyDate())
                .difficulty(request.getDifficulty())
                .user(user)
                .studyGroup(group)
                .build();

        return mapToResponse(progressRepository.save(entry));
    }

    @Override
    public List<ProgressEntryResponse> getMyProgress(String email) {
        User user = getUserByEmail(email);
        return progressRepository.findByUserIdOrderByStudyDateDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProgressEntryResponse> getProgressThisWeek(String email) {
        User user = getUserByEmail(email);
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);

        return progressRepository
                .findByUserIdAndStudyDateBetweenOrderByStudyDateDesc(
                        user.getId(), weekStart, today)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProgressSummaryResponse getMySummary(String email) {
        User user = getUserByEmail(email);
        Long userId = user.getId();

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);

        // Total stats
        Integer totalMinutes = progressRepository.sumStudyMinutesByUserId(userId);
        Integer totalTopics  = progressRepository.countTopicsByUserId(userId);
        Integer weekMinutes  = progressRepository.sumMinutesBetweenDates(
                userId, weekStart, today);

        // Safe null handling — SUM returns null when no rows exist
        totalMinutes = totalMinutes != null ? totalMinutes : 0;
        totalTopics  = totalTopics  != null ? totalTopics  : 0;
        weekMinutes  = weekMinutes  != null ? weekMinutes  : 0;

        // Count topics completed this week
        List<ProgressEntry> weekEntries = progressRepository
                .findByUserIdAndStudyDateBetweenOrderByStudyDateDesc(
                        userId, weekStart, today);
        int weekTopics = weekEntries.size();

        // Build last 7 days breakdown
        List<DailyProgressResponse> weeklyBreakdown = buildWeeklyBreakdown(
                userId, today);

        // Calculate streak
        int streak = calculateStreak(userId);

        return ProgressSummaryResponse.builder()
                .totalTopicsCompleted(totalTopics)
                .totalStudyMinutes(totalMinutes)
                .totalStudyHours(totalMinutes / 60)
                .currentWeekMinutes(weekMinutes)
                .currentWeekTopics(weekTopics)
                .currentStreakDays(streak)
                .weeklyBreakdown(weeklyBreakdown)
                .build();
    }

    @Override
    @Transactional
    public void deleteEntry(String email, Long entryId) {
        User user = getUserByEmail(email);
        ProgressEntry entry = progressRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress entry not found"));

        if (!entry.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only delete your own progress entries");
        }

        progressRepository.delete(entry);
    }

    // ── Private helpers ────────────────────────────────────────────

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Builds a day-by-day breakdown for the last 7 days.
     * Even days with no activity are included (0 minutes).
     * This is what populates the weekly activity chart on the frontend.
     */
    private List<DailyProgressResponse> buildWeeklyBreakdown(Long userId, LocalDate today) {
        List<DailyProgressResponse> breakdown = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);

            List<ProgressEntry> dayEntries = progressRepository
                    .findByUserIdAndStudyDateBetweenOrderByStudyDateDesc(
                            userId, date, date);

            int dayMinutes = dayEntries.stream()
                    .mapToInt(ProgressEntry::getStudyMinutes)
                    .sum();

            breakdown.add(DailyProgressResponse.builder()
                    .date(date)
                    .minutesStudied(dayMinutes)
                    .topicsCompleted(dayEntries.size())
                    .build());
        }

        return breakdown;
    }

    /**
     * Calculates consecutive days with at least one logged activity.
     *
     * Algorithm:
     * 1. Get all distinct study dates, sorted descending
     * 2. Start from today (or yesterday if today has no activity)
     * 3. Count how many consecutive days going backwards have activity
     */
    private int calculateStreak(Long userId) {
        List<LocalDate> activityDates = progressRepository
                .findDistinctStudyDatesByUserId(userId);

        if (activityDates.isEmpty()) return 0;

        Set<LocalDate> dateSet = activityDates.stream().collect(Collectors.toSet());

        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate checkDate = today;

        // If no activity today, check if yesterday starts the streak
        if (!dateSet.contains(today)) {
            checkDate = today.minusDays(1);
        }

        while (dateSet.contains(checkDate)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }

        return streak;
    }

    private ProgressEntryResponse mapToResponse(ProgressEntry entry) {
        return ProgressEntryResponse.builder()
                .id(entry.getId())
                .topicName(entry.getTopicName())
                .description(entry.getDescription())
                .studyMinutes(entry.getStudyMinutes())
                .studyDate(entry.getStudyDate())
                .difficulty(entry.getDifficulty().name())
                .groupId(entry.getStudyGroup() != null
                        ? entry.getStudyGroup().getId() : null)
                .groupName(entry.getStudyGroup() != null
                        ? entry.getStudyGroup().getName() : null)
                .createdAt(entry.getCreatedAt())
                .build();
    }
}