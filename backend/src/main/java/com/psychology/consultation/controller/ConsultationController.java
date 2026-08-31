package com.psychology.consultation.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psychology.consultation.model.ConsultationSessionResponse;
import com.psychology.consultation.model.SendMessageRequest;
import com.psychology.consultation.service.ConsultationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    @GetMapping
    public List<ConsultationSessionResponse> list(Authentication authentication) {
        return consultationService.listSessions(authentication.getName());
    }

    @PostMapping
    public ConsultationSessionResponse create(Authentication authentication) {
        return consultationService.createSession(authentication.getName());
    }

    @GetMapping("/{sessionId}")
    public ConsultationSessionResponse get(
            Authentication authentication,
            @PathVariable Long sessionId) {
        return consultationService.getSession(authentication.getName(), sessionId);
    }

    @PostMapping("/{sessionId}/messages")
    public ConsultationSessionResponse sendMessage(
            Authentication authentication,
            @PathVariable Long sessionId,
            @Valid @RequestBody SendMessageRequest request) {
        return consultationService.sendMessage(authentication.getName(), sessionId, request.content());
    }
}
