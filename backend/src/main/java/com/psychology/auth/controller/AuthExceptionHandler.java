package com.psychology.auth.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.psychology.auth.exception.InvalidCredentialsException;
import com.psychology.auth.exception.AuthenticatedUserNotFoundException;
import com.psychology.auth.exception.UsernameAlreadyExistsException;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleInvalidCredentials() {
        return Map.of("status", 401, "message", "invalid username or password");
    }

    @ExceptionHandler(AuthenticatedUserNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleAuthenticatedUserNotFound() {
        return Map.of("status", 401, "message", "authenticated user no longer exists");
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleUsernameAlreadyExists() {
        return Map.of("status", 409, "message", "username already exists");
    }
}
