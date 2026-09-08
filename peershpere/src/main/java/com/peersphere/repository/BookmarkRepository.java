package com.peersphere.repository;

import com.peersphere.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByNoteIdAndUserId(Long noteId, Long userId);
    Optional<Bookmark> findByNoteIdAndUserId(Long noteId, Long userId);
    List<Bookmark> findByUserIdOrderBySavedAtDesc(Long userId);
}