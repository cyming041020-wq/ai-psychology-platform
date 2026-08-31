package com.psychology.auth.exception;

public class AuthenticatedUserNotFoundException extends RuntimeException {

    public AuthenticatedUserNotFoundException() {
        super("authenticated user no longer exists");
    }
}
