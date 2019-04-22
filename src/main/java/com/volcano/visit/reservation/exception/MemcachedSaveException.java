package com.volcano.visit.reservation.exception;

public class MemcachedSaveException extends RuntimeException {

    public MemcachedSaveException() {
    }

    public MemcachedSaveException(String message) {
        super(message);
    }

    public MemcachedSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
