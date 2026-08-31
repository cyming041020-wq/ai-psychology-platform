package com.psychology.consultation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.psychology.auth.entity.SysUser;
import com.psychology.auth.service.AuthService;
import com.psychology.consultation.entity.ConsultationMessage;
import com.psychology.consultation.entity.ConsultationSession;
import com.psychology.consultation.exception.ConsultationSessionNotFoundException;
import com.psychology.consultation.mapper.ConsultationMessageMapper;
import com.psychology.consultation.mapper.ConsultationSessionMapper;
import com.psychology.consultation.model.ConsultationSessionResponse;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {

    @Mock
    private AuthService authService;

    @Mock
    private ConsultationSessionMapper sessionMapper;

    @Mock
    private ConsultationMessageMapper messageMapper;

    @Mock
    private AiReplyService aiReplyService;

    @InjectMocks
    private ConsultationService consultationService;

    @Test
    void createsActiveAiSessionForCurrentUser() {
        SysUser user = user(7L, "user@example.com");
        when(authService.requireActiveUser("user@example.com")).thenReturn(user);
        when(sessionMapper.insert(any())).thenAnswer(invocation -> {
            ConsultationSession session = invocation.getArgument(0);
            session.setId(12L);
            return 1;
        });

        ConsultationSessionResponse response = consultationService.createSession("user@example.com");

        assertThat(response.id()).isEqualTo(12L);
        assertThat(response.sessionType()).isEqualTo("AI");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.messages()).isEmpty();
    }

    @Test
    void persistsUserAndAssistantMessagesInOneConversationTurn() {
        SysUser user = user(7L, "user@example.com");
        ConsultationSession session = session(12L, 7L, "ACTIVE");
        when(authService.requireActiveUser("user@example.com")).thenReturn(user);
        when(sessionMapper.selectOne(any())).thenReturn(session);
        when(aiReplyService.reply("I feel stressed")).thenReturn("I hear you.");
        when(messageMapper.selectList(any())).thenReturn(List.of(
                message(21L, 12L, "USER", "I feel stressed"),
                message(22L, 12L, "ASSISTANT", "I hear you.")));

        ConsultationSessionResponse response = consultationService.sendMessage(
                "user@example.com", 12L, " I feel stressed ");

        assertThat(response.messages()).extracting("role")
                .containsExactly("USER", "ASSISTANT");
        assertThat(response.messages()).extracting("content")
                .containsExactly("I feel stressed", "I hear you.");
    }

    @Test
    void hidesSessionsThatDoNotBelongToCurrentUser() {
        when(authService.requireActiveUser("user@example.com")).thenReturn(user(7L, "user@example.com"));
        when(sessionMapper.selectOne(any())).thenReturn(null);

        assertThatThrownBy(() -> consultationService.getSession("user@example.com", 99L))
                .isInstanceOf(ConsultationSessionNotFoundException.class);
    }

    private SysUser user(Long id, String username) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setUsername(username);
        user.setStatus(1);
        return user;
    }

    private ConsultationSession session(Long id, Long userId, String status) {
        ConsultationSession session = new ConsultationSession();
        session.setId(id);
        session.setUserId(userId);
        session.setSessionType("AI");
        session.setStatus(status);
        session.setStartedAt(LocalDateTime.now());
        return session;
    }

    private ConsultationMessage message(Long id, Long sessionId, String role, String content) {
        ConsultationMessage message = new ConsultationMessage();
        message.setId(id);
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }
}
