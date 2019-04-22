package com.volcano.visit.reservation.exception;

public class InvalidDateRangeException extends RuntimeException {

    public InvalidDateRangeException() {
    }

    public InvalidDateRangeException(String message) {
        super(message);
    }

    public InvalidDateRangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
