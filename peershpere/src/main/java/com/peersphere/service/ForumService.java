package com.peersphere.service;

import com.peersphere.dto.request.*;
import com.peersphere.dto.response.*;

import java.util.List;

public interface ForumService {

    QuestionResponse askQuestion(String email, AskQuestionRequest request);

    QuestionResponse getQuestionById(String email, Long questionId);

    List<QuestionResponse> getAllQuestions(String email);

    List<QuestionResponse> getQuestionsByGroup(String email, Long groupId);

    List<QuestionResponse> getUnansweredQuestions(String email);

    List<QuestionResponse> searchQuestions(String email, String keyword);

    List<QuestionResponse> getMyQuestions(String email);

    QuestionResponse updateQuestion(String email, Long questionId,
                                    AskQuestionRequest request);

    void deleteQuestion(String email, Long questionId);

    QuestionResponse toggleQuestionUpvote(String email, Long questionId);

    AnswerResponse postAnswer(String email, Long questionId,
                              PostAnswerRequest request);

    AnswerResponse updateAnswer(String email, Long answerId,
                                PostAnswerRequest request);

    void deleteAnswer(String email, Long answerId);

    AnswerResponse toggleAnswerUpvote(String email, Long answerId);

    QuestionResponse acceptAnswer(String email, Long questionId, Long answerId);

    CommentResponse addQuestionComment(String email, Long questionId,
                                       PostCommentRequest request);

    CommentResponse addAnswerComment(String email, Long answerId,
                                     PostCommentRequest request);

    void deleteQuestionComment(String email, Long commentId);

    void deleteAnswerComment(String email, Long commentId);
}