package com.peersphere.repository;

import com.peersphere.entity.ResourceLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceLikeRepository extends JpaRepository<ResourceLike, Long> {
    boolean existsByResourceIdAndUserId(Long resourceId, Long userId);
    Optional<ResourceLike> findByResourceIdAndUserId(Long resourceId, Long userId);
}