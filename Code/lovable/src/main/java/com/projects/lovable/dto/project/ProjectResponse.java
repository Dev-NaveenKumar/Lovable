package com.projects.lovable.dto.project;

import com.projects.lovable.dto.auth.UserProfileReponse;

import java.time.Instant;

public record ProjectResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        UserProfileReponse owner
) {
}
