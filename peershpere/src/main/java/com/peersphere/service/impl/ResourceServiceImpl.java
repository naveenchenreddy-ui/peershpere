package com.peersphere.service.impl;

import com.peersphere.dto.request.CreateResourceRequest;
import com.peersphere.dto.request.UpdateResourceRequest;
import com.peersphere.dto.response.ResourceResponse;
import com.peersphere.entity.*;
import com.peersphere.exception.ResourceNotFoundException;
import com.peersphere.exception.UnauthorizedException;
import com.peersphere.repository.*;
import com.peersphere.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceLikeRepository resourceLikeRepository;
    private final ResourceBookmarkRepository resourceBookmarkRepository;
    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;

    @Override
    @Transactional
    public ResourceResponse shareResource(String email, CreateResourceRequest request) {
        User user = getUserByEmail(email);

        StudyGroup group = null;
        if (request.getGroupId() != null) {
            group = studyGroupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        }

        Resource resource = Resource.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .url(request.getUrl())
                .resourceType(request.getResourceType())
                .tags(request.getTags())
                .sharedBy(user)
                .studyGroup(group)
                .build();

        return mapToResponse(resourceRepository.save(resource), user.getId());
    }

    @Override
    public ResourceResponse getResourceById(String email, Long resourceId) {
        User user = getUserByEmail(email);
        return mapToResponse(getResourceOrThrow(resourceId), user.getId());
    }

    @Override
    public List<ResourceResponse> getResourcesByGroup(String email, Long groupId) {
        User user = getUserByEmail(email);
        return resourceRepository.findByStudyGroupIdOrderByCreatedAtDesc(groupId)
                .stream()
                .map(r -> mapToResponse(r, user.getId()))
                .toList();
    }

    @Override
    public List<ResourceResponse> getPublicResources(String email) {
        User user = getUserByEmail(email);
        return resourceRepository.findByStudyGroupIsNullOrderByCreatedAtDesc()
                .stream()
                .map(r -> mapToResponse(r, user.getId()))
                .toList();
    }

    @Override
    public List<ResourceResponse> getMyResources(String email) {
        User user = getUserByEmail(email);
        return resourceRepository.findBySharedByIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(r -> mapToResponse(r, user.getId()))
                .toList();
    }

    @Override
    public List<ResourceResponse> getResourcesByType(String email, ResourceType type) {
        User user = getUserByEmail(email);
        return resourceRepository.findByResourceTypeOrderByCreatedAtDesc(type)
                .stream()
                .map(r -> mapToResponse(r, user.getId()))
                .toList();
    }

    @Override
    public List<ResourceResponse> searchResources(String email, String keyword) {
        User user = getUserByEmail(email);
        return resourceRepository.searchResources(keyword)
                .stream()
                .map(r -> mapToResponse(r, user.getId()))
                .toList();
    }

    @Override
    @Transactional
    public ResourceResponse updateResource(String email, Long resourceId,
                                           UpdateResourceRequest request) {
        User user = getUserByEmail(email);
        Resource resource = getResourceOrThrow(resourceId);

        if (!resource.getSharedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only the resource owner can edit it");
        }

        if (request.getTitle() != null)        resource.setTitle(request.getTitle());
        if (request.getDescription() != null)  resource.setDescription(request.getDescription());
        if (request.getTags() != null)         resource.setTags(request.getTags());
        if (request.getResourceType() != null) resource.setResourceType(request.getResourceType());

        return mapToResponse(resourceRepository.save(resource), user.getId());
    }

    @Override
    @Transactional
    public void deleteResource(String email, Long resourceId) {
        User user = getUserByEmail(email);
        Resource resource = getResourceOrThrow(resourceId);

        if (!resource.getSharedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only the resource owner can delete it");
        }

        resourceRepository.delete(resource);
    }

    @Override
    @Transactional
    public ResourceResponse toggleLike(String email, Long resourceId) {
        User user = getUserByEmail(email);
        Resource resource = getResourceOrThrow(resourceId);

        if (resourceLikeRepository.existsByResourceIdAndUserId(resourceId, user.getId())) {
            ResourceLike like = resourceLikeRepository
                    .findByResourceIdAndUserId(resourceId, user.getId()).get();
            resourceLikeRepository.delete(like);
            resource.setLikeCount(Math.max(0, resource.getLikeCount() - 1));
        } else {
            ResourceLike like = ResourceLike.builder()
                    .resource(resource)
                    .user(user)
                    .build();
            resourceLikeRepository.save(like);
            resource.setLikeCount(resource.getLikeCount() + 1);
        }

        return mapToResponse(resourceRepository.save(resource), user.getId());
    }

    @Override
    @Transactional
    public ResourceResponse toggleBookmark(String email, Long resourceId) {
        User user = getUserByEmail(email);
        Resource resource = getResourceOrThrow(resourceId);

        if (resourceBookmarkRepository.existsByResourceIdAndUserId(
                resourceId, user.getId())) {
            ResourceBookmark bookmark = resourceBookmarkRepository
                    .findByResourceIdAndUserId(resourceId, user.getId()).get();
            resourceBookmarkRepository.delete(bookmark);
        } else {
            ResourceBookmark bookmark = ResourceBookmark.builder()
                    .resource(resource)
                    .user(user)
                    .build();
            resourceBookmarkRepository.save(bookmark);
        }

        return mapToResponse(resource, user.getId());
    }

    @Override
    public List<ResourceResponse> getMyBookmarks(String email) {
        User user = getUserByEmail(email);
        return resourceBookmarkRepository
                .findByUserIdOrderBySavedAtDesc(user.getId())
                .stream()
                .map(b -> mapToResponse(b.getResource(), user.getId()))
                .toList();
    }

    @Override
    @Transactional
    public ResourceResponse trackView(String email, Long resourceId) {
        User user = getUserByEmail(email);
        Resource resource = getResourceOrThrow(resourceId);
        resource.setViewCount(resource.getViewCount() + 1);
        return mapToResponse(resourceRepository.save(resource), user.getId());
    }

    // ── Private helpers ────────────────────────────────────────────

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Resource getResourceOrThrow(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource not found with id: " + resourceId));
    }

    private ResourceResponse mapToResponse(Resource r, Long currentUserId) {
        return ResourceResponse.builder()
                .id(r.getId())
                .title(r.getTitle())
                .description(r.getDescription())
                .url(r.getUrl())
                .resourceType(r.getResourceType().name())
                .tags(r.getTags())
                .viewCount(r.getViewCount())
                .likeCount(r.getLikeCount())
                .sharedById(r.getSharedBy().getId())
                .sharedByName(r.getSharedBy().getFullName())
                .groupId(r.getStudyGroup() != null
                        ? r.getStudyGroup().getId() : null)
                .groupName(r.getStudyGroup() != null
                        ? r.getStudyGroup().getName() : null)
                .likedByCurrentUser(resourceLikeRepository
                        .existsByResourceIdAndUserId(r.getId(), currentUserId))
                .bookmarkedByCurrentUser(resourceBookmarkRepository
                        .existsByResourceIdAndUserId(r.getId(), currentUserId))
                .createdAt(r.getCreatedAt())
                .build();
    }
}