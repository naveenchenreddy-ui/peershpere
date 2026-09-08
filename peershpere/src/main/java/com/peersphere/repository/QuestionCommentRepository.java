package com.peersphere.repository;

import com.peersphere.entity.QuestionComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionCommentRepository extends JpaRepository<QuestionComment, Long> {
    List<QuestionComment> findByQuestionIdOrderByCreatedAtAsc(Long questionId);
}