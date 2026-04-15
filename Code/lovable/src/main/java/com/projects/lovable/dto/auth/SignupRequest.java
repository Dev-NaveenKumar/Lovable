package com.projects.lovable.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @Size(min = 4, max = 50) String password
) {
}
