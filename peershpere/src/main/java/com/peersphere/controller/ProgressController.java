package com.peersphere.controller;

import com.peersphere.dto.request.LogProgressRequest;
import com.peersphere.dto.response.ProgressEntryResponse;
import com.peersphere.dto.response.ProgressSummaryResponse;
import com.peersphere.service.ProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping
    public ResponseEntity<ProgressEntryResponse> logProgress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody LogProgressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(progressService.logProgress(userDetails.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<List<ProgressEntryResponse>> getMyProgress(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(progressService.getMyProgress(userDetails.getUsername()));
    }

    @GetMapping("/this-week")
    public ResponseEntity<List<ProgressEntryResponse>> getProgressThisWeek(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                progressService.getProgressThisWeek(userDetails.getUsername()));
    }

    @GetMapping("/summary")
    public ResponseEntity<ProgressSummaryResponse> getMySummary(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                progressService.getMySummary(userDetails.getUsername()));
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> deleteEntry(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long entryId) {
        progressService.deleteEntry(userDetails.getUsername(), entryId);
        return ResponseEntity.noContent().build();
    }
}