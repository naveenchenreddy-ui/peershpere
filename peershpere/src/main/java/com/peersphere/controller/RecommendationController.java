package com.peersphere.controller;

import com.peersphere.dto.response.PeerRecommendationResponse;
import com.peersphere.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * GET /api/recommendations/peers
     * Returns up to 10 recommended peers for the current user.
     * This is what powers the "Recommended Peers" dashboard widget.
     *
     * No parameters needed — we identify the user from their JWT token
     * and the algorithm does the rest automatically.
     */
    @GetMapping("/peers")
    public ResponseEntity<List<PeerRecommendationResponse>> getRecommendedPeers(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                recommendationService.getRecommendedPeers(
                        userDetails.getUsername()));
    }
}