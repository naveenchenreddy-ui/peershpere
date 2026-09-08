package com.peersphere.service;

import com.peersphere.dto.request.LogProgressRequest;
import com.peersphere.dto.response.ProgressEntryResponse;
import com.peersphere.dto.response.ProgressSummaryResponse;

import java.util.List;

public interface ProgressService {
    ProgressEntryResponse logProgress(String email, LogProgressRequest request);
    List<ProgressEntryResponse> getMyProgress(String email);
    List<ProgressEntryResponse> getProgressThisWeek(String email);
    ProgressSummaryResponse getMySummary(String email);
    void deleteEntry(String email, Long entryId);
}