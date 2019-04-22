package com.volcano.visit.reservation.exception;

import java.time.LocalDate;
import java.util.List;

public class NoVacancyException extends RuntimeException {

    public NoVacancyException() {
    }

    public NoVacancyException(String message) {
        super(message);
    }

    public NoVacancyException(String message, Throwable cause) {
        super(message, cause);
    }
}
