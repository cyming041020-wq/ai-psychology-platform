package com.psychology.consultation.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.psychology.consultation.exception.ConsultationSessionClosedException;
import com.psychology.consultation.exception.ConsultationSessionNotFoundException;

@RestControllerAdvice
public class ConsultationExceptionHandler {

    @ExceptionHandler(ConsultationSessionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleSessionNotFound() {
        return Map.of("status", 404, "message", "consultation session not found");
    }

    @ExceptionHandler(ConsultationSessionClosedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleSessionClosed() {
        return Map.of("status", 409, "message", "consultation session is no longer active");
    }
}
