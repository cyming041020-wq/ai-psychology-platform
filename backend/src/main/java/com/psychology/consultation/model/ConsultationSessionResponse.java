package com.psychology.consultation.model;

import java.time.LocalDateTime;
import java.util.List;

public record ConsultationSessionResponse(
        Long id,
        String sessionType,
        String status,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        List<MessageResponse> messages) {
}
