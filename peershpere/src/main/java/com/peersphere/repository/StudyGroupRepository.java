package com.peersphere.repository;

import com.peersphere.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    /**
     * Find all active groups — for the "Browse Groups" page.
     */
    List<StudyGroup> findByIsActiveTrue();

    /**
     * Search groups by name or subject keyword.
     */
    @Query("SELECT g FROM StudyGroup g WHERE " +
            "LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(g.subject) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<StudyGroup> searchByNameOrSubject(@Param("keyword") String keyword);

    /**
     * Find all groups a specific user has created.
     */
    List<StudyGroup> findByCreatedById(Long userId);
}