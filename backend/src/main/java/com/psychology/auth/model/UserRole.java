package com.psychology.auth.model;

import java.util.Arrays;

public enum UserRole {
    USER,
    COUNSELOR,
    ADMIN;

    public static boolean isSupported(String value) {
        return value != null && Arrays.stream(values())
                .anyMatch(role -> role.name().equals(value));
    }
}
