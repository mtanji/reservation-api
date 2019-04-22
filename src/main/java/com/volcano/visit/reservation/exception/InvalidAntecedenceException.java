package com.volcano.visit.reservation.exception;

public class InvalidAntecedenceException extends RuntimeException {

    public InvalidAntecedenceException() {
    }

    public InvalidAntecedenceException(String message) {
        super(message);
    }

    public InvalidAntecedenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
