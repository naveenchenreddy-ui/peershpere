package com.peersphere.service.impl;

import com.peersphere.dto.request.*;
import com.peersphere.dto.response.*;
import com.peersphere.entity.*;
import com.peersphere.exception.ResourceNotFoundException;
import com.peersphere.exception.UnauthorizedException;
import com.peersphere.repository.*;
import com.peersphere.service.ForumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumServiceImpl implements ForumService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionCommentRepository questionCommentRepository;
    private final AnswerCommentRepository answerCommentRepository;
    private final QuestionUpvoteRepository questionUpvoteRepository;
    private final AnswerUpvoteRepository answerUpvoteRepository;
    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;

    @Override
    @Transactional
    public QuestionResponse askQuestion(String email, AskQuestionRequest request) {
        User asker = getUserByEmail(email);

        StudyGroup group = null;
        if (request.getGroupId() != null) {
            group = studyGroupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        }

        Question question = Question.builder()
                .title(request.getTitle())
                .body(request.getBody())
                .tags(request.getTags())
                .askedBy(asker)
                .studyGroup(group)
                .build();

        return mapToQuestionResponse(questionRepository.save(question), asker.getId());
    }

    @Override
    public QuestionResponse getQuestionById(String email, Long questionId) {
        User user = getUserByEmail(email);
        return mapToQuestionResponse(getQuestionOrThrow(questionId), user.getId());
    }

    @Override
    public List<QuestionResponse> getAllQuestions(String email) {
        User user = getUserByEmail(email);
        return questionRepository.findByStudyGroupIsNullOrderByCreatedAtDesc()
                .stream()
                .map(q -> mapToQuestionResponse(q, user.getId()))
                .toList();
    }

    @Override
    public List<QuestionResponse> getQuestionsByGroup(String email, Long groupId) {
        User user = getUserByEmail(email);
        return questionRepository.findByStudyGroupIdOrderByCreatedAtDesc(groupId)
                .stream()
                .map(q -> mapToQuestionResponse(q, user.getId()))
                .toList();
    }

    @Override
    public List<QuestionResponse> getUnansweredQuestions(String email) {
        User user = getUserByEmail(email);
        return questionRepository.findByAnswerCountOrderByCreatedAtDesc(0)
                .stream()
                .map(q -> mapToQuestionResponse(q, user.getId()))
                .toList();
    }

    @Override
    public List<QuestionResponse> searchQuestions(String email, String keyword) {
        User user = getUserByEmail(email);
        return questionRepository.searchQuestions(keyword)
                .stream()
                .map(q -> mapToQuestionResponse(q, user.getId()))
                .toList();
    }

    @Override
    public List<QuestionResponse> getMyQuestions(String email) {
        User user = getUserByEmail(email);
        return questionRepository.findByAskedByIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(q -> mapToQuestionResponse(q, user.getId()))
                .toList();
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(String email, Long questionId,
                                           AskQuestionRequest request) {
        User user = getUserByEmail(email);
        Question question = getQuestionOrThrow(questionId);

        verifyQuestionOwner(question, user.getId());

        if (request.getTitle() != null) question.setTitle(request.getTitle());
        if (request.getBody() != null)  question.setBody(request.getBody());
        if (request.getTags() != null)  question.setTags(request.getTags());

        return mapToQuestionResponse(questionRepository.save(question), user.getId());
    }

    @Override
    @Transactional
    public void deleteQuestion(String email, Long questionId) {
        User user = getUserByEmail(email);
        Question question = getQuestionOrThrow(questionId);
        verifyQuestionOwner(question, user.getId());
        questionRepository.delete(question);
    }

    @Override
    @Transactional
    public QuestionResponse toggleQuestionUpvote(String email, Long questionId) {
        User user = getUserByEmail(email);
        Question question = getQuestionOrThrow(questionId);

        if (questionUpvoteRepository.existsByQuestionIdAndUserId(questionId, user.getId())) {
            QuestionUpvote upvote = questionUpvoteRepository
                    .findByQuestionIdAndUserId(questionId, user.getId()).get();
            questionUpvoteRepository.delete(upvote);
            question.setUpvoteCount(Math.max(0, question.getUpvoteCount() - 1));
        } else {
            QuestionUpvote upvote = QuestionUpvote.builder()
                    .question(question)
                    .user(user)
                    .build();
            questionUpvoteRepository.save(upvote);
            question.setUpvoteCount(question.getUpvoteCount() + 1);
        }

        return mapToQuestionResponse(questionRepository.save(question), user.getId());
    }

    @Override
    @Transactional
    public AnswerResponse postAnswer(String email, Long questionId,
                                     PostAnswerRequest request) {
        User answerer = getUserByEmail(email);
        Question question = getQuestionOrThrow(questionId);

        Answer answer = Answer.builder()
                .body(request.getBody())
                .question(question)
                .answeredBy(answerer)
                .build();

        Answer saved = answerRepository.save(answer);

        // Increment the answer count on the question
        question.setAnswerCount(question.getAnswerCount() + 1);
        questionRepository.save(question);

        return mapToAnswerResponse(saved, answerer.getId());
    }

    @Override
    @Transactional
    public AnswerResponse updateAnswer(String email, Long answerId,
                                       PostAnswerRequest request) {
        User user = getUserByEmail(email);
        Answer answer = getAnswerOrThrow(answerId);

        if (!answer.getAnsweredBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only the answer author can edit this answer");
        }

        answer.setBody(request.getBody());
        return mapToAnswerResponse(answerRepository.save(answer), user.getId());
    }

    @Override
    @Transactional
    public void deleteAnswer(String email, Long answerId) {
        User user = getUserByEmail(email);
        Answer answer = getAnswerOrThrow(answerId);

        if (!answer.getAnsweredBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only the answer author can delete this answer");
        }

        // Decrement answer count
        Question question = answer.getQuestion();
        question.setAnswerCount(Math.max(0, question.getAnswerCount() - 1));
        questionRepository.save(question);

        answerRepository.delete(answer);
    }

    @Override
    @Transactional
    public AnswerResponse toggleAnswerUpvote(String email, Long answerId) {
        User user = getUserByEmail(email);
        Answer answer = getAnswerOrThrow(answerId);

        if (answerUpvoteRepository.existsByAnswerIdAndUserId(answerId, user.getId())) {
            AnswerUpvote upvote = answerUpvoteRepository
                    .findByAnswerIdAndUserId(answerId, user.getId()).get();
            answerUpvoteRepository.delete(upvote);
            answer.setUpvoteCount(Math.max(0, answer.getUpvoteCount() - 1));
        } else {
            AnswerUpvote upvote = AnswerUpvote.builder()
                    .answer(answer)
                    .user(user)
                    .build();
            answerUpvoteRepository.save(upvote);
            answer.setUpvoteCount(answer.getUpvoteCount() + 1);
        }

        return mapToAnswerResponse(answerRepository.save(answer), user.getId());
    }

    @Override
    @Transactional
    public QuestionResponse acceptAnswer(String email, Long questionId, Long answerId) {
        User user = getUserByEmail(email);
        Question question = getQuestionOrThrow(questionId);

        // Only the question asker can accept an answer
        if (!question.getAskedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only the question author can accept an answer");
        }

        // Unaccept any previously accepted answer first
        answerRepository.findByQuestionIdAndIsAcceptedTrue(questionId)
                .ifPresent(prev -> {
                    prev.setIsAccepted(false);
                    answerRepository.save(prev);
                });

        // Accept the new answer
        Answer answer = getAnswerOrThrow(answerId);
        if (!answer.getQuestion().getId().equals(questionId)) {
            throw new IllegalArgumentException("This answer does not belong to this question");
        }

        answer.setIsAccepted(true);
        answerRepository.save(answer);

        // Mark question as solved
        question.setIsSolved(true);
        return mapToQuestionResponse(questionRepository.save(question), user.getId());
    }

    @Override
    @Transactional
    public CommentResponse addQuestionComment(String email, Long questionId,
                                              PostCommentRequest request) {
        User user = getUserByEmail(email);
        Question question = getQuestionOrThrow(questionId);

        QuestionComment comment = QuestionComment.builder()
                .body(request.getBody())
                .question(question)
                .commentedBy(user)
                .build();

        QuestionComment saved = questionCommentRepository.save(comment);
        return mapToCommentResponse(saved.getId(), saved.getBody(),
                user.getId(), user.getFullName(), saved.getCreatedAt());
    }

    @Override
    @Transactional
    public CommentResponse addAnswerComment(String email, Long answerId,
                                            PostCommentRequest request) {
        User user = getUserByEmail(email);
        Answer answer = getAnswerOrThrow(answerId);

        AnswerComment comment = AnswerComment.builder()
                .body(request.getBody())
                .answer(answer)
                .commentedBy(user)
                .build();

        AnswerComment saved = answerCommentRepository.save(comment);
        return mapToCommentResponse(saved.getId(), saved.getBody(),
                user.getId(), user.getFullName(), saved.getCreatedAt());
    }

    @Override
    @Transactional
    public void deleteQuestionComment(String email, Long commentId) {
        User user = getUserByEmail(email);
        QuestionComment comment = questionCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        if (!comment.getCommentedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only the comment author can delete this comment");
        }
        questionCommentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void deleteAnswerComment(String email, Long commentId) {
        User user = getUserByEmail(email);
        AnswerComment comment = answerCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        if (!comment.getCommentedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only the comment author can delete this comment");
        }
        answerCommentRepository.delete(comment);
    }

    // ── Private helpers ────────────────────────────────────────────

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Question getQuestionOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
    }

    private Answer getAnswerOrThrow(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + id));
    }

    private void verifyQuestionOwner(Question question, Long userId) {
        if (!question.getAskedBy().getId().equals(userId)) {
            throw new UnauthorizedException("Only the question author can perform this action");
        }
    }

    private QuestionResponse mapToQuestionResponse(Question q, Long currentUserId) {
        List<AnswerResponse> answerResponses = q.getAnswers()
                .stream()
                .map(a -> mapToAnswerResponse(a, currentUserId))
                .toList();

        List<CommentResponse> commentResponses = q.getComments()
                .stream()
                .map(c -> mapToCommentResponse(
                        c.getId(), c.getBody(),
                        c.getCommentedBy().getId(),
                        c.getCommentedBy().getFullName(),
                        c.getCreatedAt()))
                .toList();

        return QuestionResponse.builder()
                .id(q.getId())
                .title(q.getTitle())
                .body(q.getBody())
                .tags(q.getTags())
                .upvoteCount(q.getUpvoteCount())
                .answerCount(q.getAnswerCount())
                .isSolved(q.getIsSolved())
                .askedById(q.getAskedBy().getId())
                .askedByName(q.getAskedBy().getFullName())
                .askedByDepartment(q.getAskedBy().getDepartment())
                .groupId(q.getStudyGroup() != null ? q.getStudyGroup().getId() : null)
                .groupName(q.getStudyGroup() != null ? q.getStudyGroup().getName() : null)
                .upvotedByCurrentUser(questionUpvoteRepository
                        .existsByQuestionIdAndUserId(q.getId(), currentUserId))
                .answers(answerResponses)
                .comments(commentResponses)
                .createdAt(q.getCreatedAt())
                .build();
    }

    private AnswerResponse mapToAnswerResponse(Answer a, Long currentUserId) {
        List<CommentResponse> comments = a.getComments()
                .stream()
                .map(c -> mapToCommentResponse(
                        c.getId(), c.getBody(),
                        c.getCommentedBy().getId(),
                        c.getCommentedBy().getFullName(),
                        c.getCreatedAt()))
                .toList();

        return AnswerResponse.builder()
                .id(a.getId())
                .body(a.getBody())
                .upvoteCount(a.getUpvoteCount())
                .isAccepted(a.getIsAccepted())
                .answeredById(a.getAnsweredBy().getId())
                .answeredByName(a.getAnsweredBy().getFullName())
                .answeredByDepartment(a.getAnsweredBy().getDepartment())
                .upvotedByCurrentUser(answerUpvoteRepository
                        .existsByAnswerIdAndUserId(a.getId(), currentUserId))
                .comments(comments)
                .createdAt(a.getCreatedAt())
                .build();
    }

    private CommentResponse mapToCommentResponse(Long id, String body,
                                                 Long commentedById, String commentedByName,
                                                 java.time.LocalDateTime createdAt) {
        return CommentResponse.builder()
                .id(id)
                .body(body)
                .commentedById(commentedById)
                .commentedByName(commentedByName)
                .createdAt(createdAt)
                .build();
    }
}