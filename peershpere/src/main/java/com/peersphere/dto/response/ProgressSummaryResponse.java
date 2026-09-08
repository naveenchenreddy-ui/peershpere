package com.peersphere.dto.response;

import lombok.*;
import java.util.List;

/**
 * Aggregated stats — computed from ProgressEntry rows.
 * This powers the Progress Summary widget on the Dashboard.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressSummaryResponse {

    private Integer totalTopicsCompleted;
    private Integer totalStudyMinutes;
    private Integer totalStudyHours;        // totalStudyMinutes / 60
    private Integer currentWeekMinutes;     // minutes logged this week
    private Integer currentWeekTopics;      // topics logged this week
    private Integer currentStreakDays;      // consecutive days with activity
    private List<DailyProgressResponse> weeklyBreakdown; // last 7 days
}