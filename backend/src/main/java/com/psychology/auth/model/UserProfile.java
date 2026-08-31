package com.psychology.auth.model;

public record UserProfile(
        Long id,
        String username,
        String displayName,
        String role) {
}
