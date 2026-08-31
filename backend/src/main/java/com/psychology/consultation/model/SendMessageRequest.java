package com.psychology.consultation.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
        @NotBlank(message = "message content is required")
        @Size(max = 2000, message = "message content must be at most 2000 characters")
        String content) {
}
