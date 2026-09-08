package com.peersphere.repository;

import com.peersphere.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionIdOrderByIsAcceptedDescUpvoteCountDesc(Long questionId);
    Optional<Answer> findByQuestionIdAndIsAcceptedTrue(Long questionId);
}