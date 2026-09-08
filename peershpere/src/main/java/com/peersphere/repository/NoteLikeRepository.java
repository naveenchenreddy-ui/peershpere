package com.peersphere.repository;

import com.peersphere.entity.NoteLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteLikeRepository extends JpaRepository<NoteLike, Long> {
    boolean existsByNoteIdAndUserId(Long noteId, Long userId);
    Optional<NoteLike> findByNoteIdAndUserId(Long noteId, Long userId);
}