package com.projects.lovable.dto.auth;

public record LoginRequest(
        String email,
        String password
) {
}
