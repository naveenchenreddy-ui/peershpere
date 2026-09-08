package com.peersphere.dto.response;

import lombok.*;
import java.time.LocalDate;

/**
 * One day's worth of activity.
 * Used to build the "activity graph" on the frontend.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyProgressResponse {
    private LocalDate date;
    private Integer minutesStudied;
    private Integer topicsCompleted;
}