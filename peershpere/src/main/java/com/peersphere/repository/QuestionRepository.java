package com.peersphere.repository;

import com.peersphere.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByStudyGroupIdOrderByCreatedAtDesc(Long groupId);

    List<Question> findByStudyGroupIsNullOrderByCreatedAtDesc();

    List<Question> findByAskedByIdOrderByCreatedAtDesc(Long userId);

    // Unanswered questions — answerCount = 0
    List<Question> findByAnswerCountOrderByCreatedAtDesc(Integer answerCount);

    @Query("SELECT q FROM Question q WHERE " +
            "LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(q.tags) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Question> searchQuestions(@Param("keyword") String keyword);
}