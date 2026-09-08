package com.peersphere.repository;

import com.peersphere.entity.ProgressEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProgressRepository extends JpaRepository<ProgressEntry, Long> {

    List<ProgressEntry> findByUserIdOrderByStudyDateDesc(Long userId);

    List<ProgressEntry> findByUserIdAndStudyDateBetweenOrderByStudyDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Sum all study minutes for a user.
     * Returns null if no entries exist — we handle this in service.
     */
    @Query("SELECT SUM(p.studyMinutes) FROM ProgressEntry p WHERE p.user.id = :userId")
    Integer sumStudyMinutesByUserId(@Param("userId") Long userId);

    /**
     * Count total topics completed.
     */
    @Query("SELECT COUNT(p) FROM ProgressEntry p WHERE p.user.id = :userId")
    Integer countTopicsByUserId(@Param("userId") Long userId);

    /**
     * Sum minutes for the current week.
     */
    @Query("SELECT SUM(p.studyMinutes) FROM ProgressEntry p " +
            "WHERE p.user.id = :userId " +
            "AND p.studyDate BETWEEN :startDate AND :endDate")
    Integer sumMinutesBetweenDates(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find all distinct dates a user has logged activity.
     * Used for streak calculation.
     */
    @Query("SELECT DISTINCT p.studyDate FROM ProgressEntry p " +
            "WHERE p.user.id = :userId ORDER BY p.studyDate DESC")
    List<LocalDate> findDistinctStudyDatesByUserId(@Param("userId") Long userId);
}