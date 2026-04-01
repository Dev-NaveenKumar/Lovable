package com.projects.lovable.dto.auth;

public record AuthResponse(
        String token,
        UserProfileReponse user
) {
}
