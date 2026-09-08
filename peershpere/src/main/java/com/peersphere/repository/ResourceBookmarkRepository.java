package com.peersphere.repository;

import com.peersphere.entity.ResourceBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceBookmarkRepository extends JpaRepository<ResourceBookmark, Long> {
    boolean existsByResourceIdAndUserId(Long resourceId, Long userId);
    Optional<ResourceBookmark> findByResourceIdAndUserId(Long resourceId, Long userId);
    List<ResourceBookmark> findByUserIdOrderBySavedAtDesc(Long userId);
}