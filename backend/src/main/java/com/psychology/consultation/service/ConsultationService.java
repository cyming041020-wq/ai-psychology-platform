package com.psychology.consultation.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.psychology.auth.entity.SysUser;
import com.psychology.auth.service.AuthService;
import com.psychology.consultation.entity.ConsultationMessage;
import com.psychology.consultation.entity.ConsultationSession;
import com.psychology.consultation.exception.ConsultationSessionClosedException;
import com.psychology.consultation.exception.ConsultationSessionNotFoundException;
import com.psychology.consultation.mapper.ConsultationMessageMapper;
import com.psychology.consultation.mapper.ConsultationSessionMapper;
import com.psychology.consultation.model.ConsultationSessionResponse;
import com.psychology.consultation.model.MessageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private static final String SESSION_TYPE_AI = "AI";
    private static final String SESSION_STATUS_ACTIVE = "ACTIVE";
    private static final String MESSAGE_ROLE_USER = "USER";
    private static final String MESSAGE_ROLE_ASSISTANT = "ASSISTANT";

    private final AuthService authService;
    private final ConsultationSessionMapper sessionMapper;
    private final ConsultationMessageMapper messageMapper;
    private final AiReplyService aiReplyService;

    @Transactional
    public ConsultationSessionResponse createSession(String username) {
        SysUser user = authService.requireActiveUser(username);
        ConsultationSession session = new ConsultationSession();
        session.setUserId(user.getId());
        session.setSessionType(SESSION_TYPE_AI);
        session.setStatus(SESSION_STATUS_ACTIVE);
        session.setStartedAt(LocalDateTime.now());
        sessionMapper.insert(session);
        return toResponse(session, List.of());
    }

    public List<ConsultationSessionResponse> listSessions(String username) {
        SysUser user = authService.requireActiveUser(username);
        return sessionMapper.selectList(Wrappers.<ConsultationSession>lambdaQuery()
                        .eq(ConsultationSession::getUserId, user.getId())
                        .orderByDesc(ConsultationSession::getStartedAt))
                .stream()
                .map(session -> toResponse(session, List.of()))
                .toList();
    }

    public ConsultationSessionResponse getSession(String username, Long sessionId) {
        SysUser user = authService.requireActiveUser(username);
        ConsultationSession session = findOwnedSession(user.getId(), sessionId);
        return toResponse(session, findMessages(sessionId));
    }

    @Transactional
    public ConsultationSessionResponse sendMessage(String username, Long sessionId, String content) {
        SysUser user = authService.requireActiveUser(username);
        ConsultationSession session = findOwnedSession(user.getId(), sessionId);
        if (!SESSION_STATUS_ACTIVE.equals(session.getStatus())) {
            throw new ConsultationSessionClosedException();
        }

        String normalizedContent = content.trim();
        saveMessage(sessionId, MESSAGE_ROLE_USER, normalizedContent);
        saveMessage(sessionId, MESSAGE_ROLE_ASSISTANT, aiReplyService.reply(normalizedContent));
        return toResponse(session, findMessages(sessionId));
    }

    private ConsultationSession findOwnedSession(Long userId, Long sessionId) {
        ConsultationSession session = sessionMapper.selectOne(Wrappers.<ConsultationSession>lambdaQuery()
                .eq(ConsultationSession::getId, sessionId)
                .eq(ConsultationSession::getUserId, userId));
        if (session == null) {
            throw new ConsultationSessionNotFoundException();
        }
        return session;
    }

    private List<ConsultationMessage> findMessages(Long sessionId) {
        return messageMapper.selectList(Wrappers.<ConsultationMessage>lambdaQuery()
                .eq(ConsultationMessage::getSessionId, sessionId)
                .orderByAsc(ConsultationMessage::getCreatedAt)
                .orderByAsc(ConsultationMessage::getId));
    }

    private void saveMessage(Long sessionId, String role, String content) {
        ConsultationMessage message = new ConsultationMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);
    }

    private ConsultationSessionResponse toResponse(
            ConsultationSession session,
            List<ConsultationMessage> messages) {
        List<MessageResponse> messageResponses = messages.stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getRole(),
                        message.getContent(),
                        message.getCreatedAt()))
                .toList();
        return new ConsultationSessionResponse(
                session.getId(),
                session.getSessionType(),
                session.getStatus(),
                session.getStartedAt(),
                session.getEndedAt(),
                messageResponses);
    }
}
