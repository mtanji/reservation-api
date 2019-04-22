package com.volcano.visit.reservation.exception;

import java.util.Date;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = {
            InvalidAntecedenceException.class,
            InvalidDateRangeException.class,
            NoVacancyException.class})
    protected ResponseEntity<ErrorDetails> handleValidationException(RuntimeException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ObjectOptimisticLockingFailureException.class)
    protected ResponseEntity<ErrorDetails> handleRaceCondition(RuntimeException ex, WebRequest request) {
        String message = "Failed saving your reservation, please try again.";
        ErrorDetails errorDetails = new ErrorDetails(new Date(), message, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {NoSuchElementException.class, ReservationNotFoundException.class})
    protected ResponseEntity<Object> handleInexistentReservation(RuntimeException ex, WebRequest request) {
        String message = "Invalid reservation number.";
        ErrorDetails errorDetails = new ErrorDetails(new Date(), message, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleSQLException(RuntimeException ex, WebRequest request) {
        Throwable thr = NestedExceptionUtils.getRootCause(ex);
        String thrMessage = thr.getMessage().toLowerCase();
        String message;
        if (thrMessage.contains("data too long")) {
            if (thrMessage.contains("full_name")) {
                message = "Name is too long.";
            } else if (thrMessage.contains("email")) {
                message = "Email is too long.";
            } else {
                message = thrMessage;
                logger.error("Unexpected exception type: ", ex);
            }
        } else {
            message = thrMessage;
            logger.error("Unexpected exception type: ", ex);
        }
        ErrorDetails errorDetails = new ErrorDetails(new Date(), message, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        String exMessage = ex.getMessage().toLowerCase();
        String message;
        if (exMessage.contains("java.time.localdate")) {
            message = "Invalid date format.";
        } else if (exMessage.contains("numeric value") || exMessage.contains("unexpected character")) {
            message = "Invalid numeric value.";
        } else {
            message = exMessage;
            logger.error("Unexpected exception type: ", ex);
        }
        ErrorDetails errorDetails = new ErrorDetails(new Date(), message, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
