package com.peersphere.repository;

import com.peersphere.entity.QuestionUpvote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionUpvoteRepository extends JpaRepository<QuestionUpvote, Long> {
    boolean existsByQuestionIdAndUserId(Long questionId, Long userId);
    Optional<QuestionUpvote> findByQuestionIdAndUserId(Long questionId, Long userId);
}