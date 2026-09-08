package com.peersphere.service;

import com.peersphere.dto.request.CreateResourceRequest;
import com.peersphere.dto.request.UpdateResourceRequest;
import com.peersphere.dto.response.ResourceResponse;
import com.peersphere.entity.ResourceType;

import java.util.List;

public interface ResourceService {

    ResourceResponse shareResource(String email, CreateResourceRequest request);

    ResourceResponse getResourceById(String email, Long resourceId);

    List<ResourceResponse> getResourcesByGroup(String email, Long groupId);

    List<ResourceResponse> getPublicResources(String email);

    List<ResourceResponse> getMyResources(String email);

    List<ResourceResponse> getResourcesByType(String email, ResourceType type);

    List<ResourceResponse> searchResources(String email, String keyword);

    ResourceResponse updateResource(String email, Long resourceId,
                                    UpdateResourceRequest request);

    void deleteResource(String email, Long resourceId);

    ResourceResponse toggleLike(String email, Long resourceId);

    ResourceResponse toggleBookmark(String email, Long resourceId);

    List<ResourceResponse> getMyBookmarks(String email);

    ResourceResponse trackView(String email, Long resourceId);
}