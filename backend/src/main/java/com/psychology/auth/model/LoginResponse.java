package com.psychology.auth.model;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserProfile user) {
}
