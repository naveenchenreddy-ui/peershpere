package com.peersphere.repository;

import com.peersphere.entity.AnswerComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerCommentRepository extends JpaRepository<AnswerComment, Long> {
    List<AnswerComment> findByAnswerIdOrderByCreatedAtAsc(Long answerId);
}