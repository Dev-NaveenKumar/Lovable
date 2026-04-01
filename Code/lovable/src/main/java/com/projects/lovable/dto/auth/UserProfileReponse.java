package com.projects.lovable.dto.auth;

public record UserProfileReponse(
        Long id,
        String email,
        String name,
        String avatarUrl
) {
}
