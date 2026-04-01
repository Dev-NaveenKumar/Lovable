package com.projects.lovable.dto.auth;

public record SignupRequest(
        String name,
        String email,
        String password
) {
}
