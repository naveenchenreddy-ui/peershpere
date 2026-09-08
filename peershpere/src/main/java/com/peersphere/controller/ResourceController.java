package com.peersphere.controller;

import com.peersphere.dto.request.CreateResourceRequest;
import com.peersphere.dto.request.UpdateResourceRequest;
import com.peersphere.dto.response.ResourceResponse;
import com.peersphere.entity.ResourceType;
import com.peersphere.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<ResourceResponse> shareResource(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceService.shareResource(
                        userDetails.getUsername(), request));
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceResponse> getResourceById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long resourceId) {
        return ResponseEntity.ok(
                resourceService.getResourceById(
                        userDetails.getUsername(), resourceId));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ResourceResponse>> getResourcesByGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        return ResponseEntity.ok(
                resourceService.getResourcesByGroup(
                        userDetails.getUsername(), groupId));
    }

    @GetMapping("/public")
    public ResponseEntity<List<ResourceResponse>> getPublicResources(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                resourceService.getPublicResources(userDetails.getUsername()));
    }

    @GetMapping("/my-resources")
    public ResponseEntity<List<ResourceResponse>> getMyResources(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                resourceService.getMyResources(userDetails.getUsername()));
    }

    /**
     * GET /api/resources/type/VIDEO
     * Filter by resource type — useful for "Browse Videos" section.
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ResourceResponse>> getByType(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable ResourceType type) {
        return ResponseEntity.ok(
                resourceService.getResourcesByType(
                        userDetails.getUsername(), type));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResourceResponse>> searchResources(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String keyword) {
        return ResponseEntity.ok(
                resourceService.searchResources(
                        userDetails.getUsername(), keyword));
    }

    @PutMapping("/{resourceId}")
    public ResponseEntity<ResourceResponse> updateResource(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long resourceId,
            @Valid @RequestBody UpdateResourceRequest request) {
        return ResponseEntity.ok(
                resourceService.updateResource(
                        userDetails.getUsername(), resourceId, request));
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long resourceId) {
        resourceService.deleteResource(userDetails.getUsername(), resourceId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{resourceId}/like")
    public ResponseEntity<ResourceResponse> toggleLike(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long resourceId) {
        return ResponseEntity.ok(
                resourceService.toggleLike(
                        userDetails.getUsername(), resourceId));
    }

    @PostMapping("/{resourceId}/bookmark")
    public ResponseEntity<ResourceResponse> toggleBookmark(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long resourceId) {
        return ResponseEntity.ok(
                resourceService.toggleBookmark(
                        userDetails.getUsername(), resourceId));
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<List<ResourceResponse>> getMyBookmarks(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                resourceService.getMyBookmarks(userDetails.getUsername()));
    }

    /**
     * POST /api/resources/{id}/view
     * Call this before redirecting to the resource URL.
     * Increments view count then returns the resource with updated count.
     */
    @PostMapping("/{resourceId}/view")
    public ResponseEntity<ResourceResponse> trackView(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long resourceId) {
        return ResponseEntity.ok(
                resourceService.trackView(
                        userDetails.getUsername(), resourceId));
    }
}