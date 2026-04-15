package com.projects.lovable.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String resourceId;

    public ResourceNotFoundException(String resourceName, String resourceId) {
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    public ResourceNotFoundException(String resourceName, Long resourceId) {
        this.resourceName = resourceName;
        this.resourceId = resourceId != null ? resourceId.toString() : null;
    }
}
