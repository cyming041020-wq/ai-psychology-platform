package com.psychology.consultation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.psychology.auth.service.JwtService;
import com.psychology.consultation.model.ConsultationSessionResponse;
import com.psychology.consultation.model.MessageResponse;
import com.psychology.consultation.service.ConsultationService;

@WebMvcTest(ConsultationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ConsultationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultationService consultationService;

    @MockBean
    private JwtService jwtService;

    @Test
    void createsSessionForAuthenticatedPrincipal() throws Exception {
        when(consultationService.createSession("user@example.com")).thenReturn(sessionResponse());

        mockMvc.perform(post("/api/consultations")
                        .principal(new UsernamePasswordAuthenticationToken("user@example.com", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void rejectsBlankMessageBeforeCallingService() throws Exception {
        mockMvc.perform(post("/api/consultations/12/messages")
                        .principal(new UsernamePasswordAuthenticationToken("user@example.com", null))
                        .contentType(APPLICATION_JSON)
                        .content("{\"content\":\" \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsConversationAfterSendingMessage() throws Exception {
        when(consultationService.sendMessage(eq("user@example.com"), eq(12L), eq("I feel stressed")))
                .thenReturn(sessionResponse());

        mockMvc.perform(post("/api/consultations/12/messages")
                        .principal(new UsernamePasswordAuthenticationToken("user@example.com", null))
                        .contentType(APPLICATION_JSON)
                        .content("{\"content\":\"I feel stressed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0].role").value("USER"))
                .andExpect(jsonPath("$.messages[1].role").value("ASSISTANT"));
    }

    private ConsultationSessionResponse sessionResponse() {
        LocalDateTime now = LocalDateTime.of(2026, 8, 31, 16, 0);
        return new ConsultationSessionResponse(
                12L,
                "AI",
                "ACTIVE",
                now,
                null,
                List.of(
                        new MessageResponse(21L, "USER", "I feel stressed", now),
                        new MessageResponse(22L, "ASSISTANT", "I hear you.", now.plusSeconds(1))));
    }
}
