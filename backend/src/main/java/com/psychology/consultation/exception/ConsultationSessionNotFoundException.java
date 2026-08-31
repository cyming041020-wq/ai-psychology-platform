package com.psychology.consultation.exception;

public class ConsultationSessionNotFoundException extends RuntimeException {

    public ConsultationSessionNotFoundException() {
        super("consultation session not found");
    }
}
