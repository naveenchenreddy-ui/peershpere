package com.peersphere.repository;

import com.peersphere.entity.AnswerUpvote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerUpvoteRepository extends JpaRepository<AnswerUpvote, Long> {
    boolean existsByAnswerIdAndUserId(Long answerId, Long userId);
    Optional<AnswerUpvote> findByAnswerIdAndUserId(Long answerId, Long userId);
}