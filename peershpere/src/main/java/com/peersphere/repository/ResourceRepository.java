package com.peersphere.repository;

import com.peersphere.entity.Resource;
import com.peersphere.entity.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByStudyGroupIdOrderByCreatedAtDesc(Long groupId);

    List<Resource> findByStudyGroupIsNullOrderByCreatedAtDesc();

    List<Resource> findBySharedByIdOrderByCreatedAtDesc(Long userId);

    List<Resource> findByResourceTypeOrderByCreatedAtDesc(ResourceType type);

    @Query("SELECT r FROM Resource r WHERE " +
            "LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.tags) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Resource> searchResources(@Param("keyword") String keyword);
}