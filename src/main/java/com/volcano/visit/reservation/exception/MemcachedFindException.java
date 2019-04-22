package com.volcano.visit.reservation.exception;

public class MemcachedFindException extends RuntimeException {

    public MemcachedFindException() {
    }

    public MemcachedFindException(String message) {
        super(message);
    }

    public MemcachedFindException(String message, Throwable cause) {
        super(message, cause);
    }
}
