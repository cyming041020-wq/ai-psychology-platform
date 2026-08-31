package com.psychology.auth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "username is required")
        @Size(max = 64, message = "username must be at most 64 characters")
        String username,

        @NotBlank(message = "password is required")
        @Size(min = 8, max = 128, message = "password must be between 8 and 128 characters")
        String password,

        @Size(max = 64, message = "display name must be at most 64 characters")
        String displayName) {
}
