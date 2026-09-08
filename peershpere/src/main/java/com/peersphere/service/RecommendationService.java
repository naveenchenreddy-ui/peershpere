package com.peersphere.service;

import com.peersphere.dto.response.PeerRecommendationResponse;
import java.util.List;

public interface RecommendationService {
    List<PeerRecommendationResponse> getRecommendedPeers(String email);
}