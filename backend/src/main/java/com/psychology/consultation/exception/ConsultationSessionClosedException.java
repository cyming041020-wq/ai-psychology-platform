package com.psychology.consultation.exception;

public class ConsultationSessionClosedException extends RuntimeException {

    public ConsultationSessionClosedException() {
        super("consultation session is no longer active");
    }
}
