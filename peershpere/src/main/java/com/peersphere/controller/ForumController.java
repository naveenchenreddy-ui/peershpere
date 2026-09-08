package com.peersphere.controller;

import com.peersphere.dto.request.*;
import com.peersphere.dto.response.*;
import com.peersphere.service.ForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;

    // ── Questions ──────────────────────────────────────────────────

    @PostMapping("/questions")
    public ResponseEntity<QuestionResponse> askQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AskQuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(forumService.askQuestion(userDetails.getUsername(), request));
    }

    @GetMapping("/questions/{questionId}")
    public ResponseEntity<QuestionResponse> getQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long questionId) {
        return ResponseEntity.ok(
                forumService.getQuestionById(userDetails.getUsername(), questionId));
    }

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionResponse>> getAllQuestions(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(forumService.getAllQuestions(userDetails.getUsername()));
    }

    @GetMapping("/questions/group/{groupId}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        return ResponseEntity.ok(
                forumService.getQuestionsByGroup(userDetails.getUsername(), groupId));
    }

    @GetMapping("/questions/unanswered")
    public ResponseEntity<List<QuestionResponse>> getUnanswered(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                forumService.getUnansweredQuestions(userDetails.getUsername()));
    }

    @GetMapping("/questions/search")
    public ResponseEntity<List<QuestionResponse>> searchQuestions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String keyword) {
        return ResponseEntity.ok(
                forumService.searchQuestions(userDetails.getUsername(), keyword));
    }

    @GetMapping("/questions/my-questions")
    public ResponseEntity<List<QuestionResponse>> getMyQuestions(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(forumService.getMyQuestions(userDetails.getUsername()));
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long questionId,
            @Valid @RequestBody AskQuestionRequest request) {
        return ResponseEntity.ok(
                forumService.updateQuestion(userDetails.getUsername(), questionId, request));
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long questionId) {
        forumService.deleteQuestion(userDetails.getUsername(), questionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/questions/{questionId}/upvote")
    public ResponseEntity<QuestionResponse> upvoteQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long questionId) {
        return ResponseEntity.ok(
                forumService.toggleQuestionUpvote(userDetails.getUsername(), questionId));
    }

    // ── Answers ────────────────────────────────────────────────────

    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<AnswerResponse> postAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long questionId,
            @Valid @RequestBody PostAnswerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(forumService.postAnswer(
                        userDetails.getUsername(), questionId, request));
    }

    @PutMapping("/answers/{answerId}")
    public ResponseEntity<AnswerResponse> updateAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long answerId,
            @Valid @RequestBody PostAnswerRequest request) {
        return ResponseEntity.ok(
                forumService.updateAnswer(userDetails.getUsername(), answerId, request));
    }

    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long answerId) {
        forumService.deleteAnswer(userDetails.getUsername(), answerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/answers/{answerId}/upvote")
    public ResponseEntity<AnswerResponse> upvoteAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long answerId) {
        return ResponseEntity.ok(
                forumService.toggleAnswerUpvote(userDetails.getUsername(), answerId));
    }

    @PatchMapping("/questions/{questionId}/answers/{answerId}/accept")
    public ResponseEntity<QuestionResponse> acceptAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long questionId,
            @PathVariable Long answerId) {
        return ResponseEntity.ok(
                forumService.acceptAnswer(
                        userDetails.getUsername(), questionId, answerId));
    }

    // ── Comments ───────────────────────────────────────────────────

    @PostMapping("/questions/{questionId}/comments")
    public ResponseEntity<CommentResponse> commentOnQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long questionId,
            @Valid @RequestBody PostCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(forumService.addQuestionComment(
                        userDetails.getUsername(), questionId, request));
    }

    @PostMapping("/answers/{answerId}/comments")
    public ResponseEntity<CommentResponse> commentOnAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long answerId,
            @Valid @RequestBody PostCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(forumService.addAnswerComment(
                        userDetails.getUsername(), answerId, request));
    }

    @DeleteMapping("/question-comments/{commentId}")
    public ResponseEntity<Void> deleteQuestionComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long commentId) {
        forumService.deleteQuestionComment(userDetails.getUsername(), commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/answer-comments/{commentId}")
    public ResponseEntity<Void> deleteAnswerComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long commentId) {
        forumService.deleteAnswerComment(userDetails.getUsername(), commentId);
        return ResponseEntity.noContent().build();
    }
}