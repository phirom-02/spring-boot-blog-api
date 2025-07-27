package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.domain.dtos.ApiErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Global error handler for the blog API.
 */
@RestController
@ControllerAdvice
@Slf4j
public class ErrorController {

    /**
     * Handles generic exceptions that are not specifically handled by other exception handlers.
     * Logs the exception and returns a 500 Internal Server Error with a generic message.
     *
     * @param e the exception that was thrown
     * @return a {@link ResponseEntity} containing an {@link ApiErrorResponse} with HTTP status 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        log.error("Exception occurred", e);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * Handles validation errors (e.g., invalid method argument) caused by invalid input in requests.
     * Logs the validation error and returns a 400 Bad Request with details of the invalid fields.
     *
     * @param e the exception that was thrown (MethodArgumentNotValidException)
     * @return a {@link ResponseEntity} containing an {@link ApiErrorResponse} with HTTP status 400 (Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException occurred", e);
        List<ApiErrorResponse.FieldError> details = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    String field = error.getField();
                    String message = error.getDefaultMessage();
                    return new ApiErrorResponse.FieldError(field, message);
                })
                .toList();

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Invalid Input")
                .details(details)
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    /**
     * Handles illegal state exceptions thrown by the application.
     * Logs the exception and returns a 400 Bad Request with the exception message.
     *
     * @param e the exception that was thrown (IllegalStateException)
     * @return a {@link ResponseEntity} containing an {@link ApiErrorResponse} with HTTP status 400 (Bad Request)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.error("IllegalStateException occurred", e);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles authentication failures, specifically incorrect email or password.
     * Logs the exception and returns a 401 Unauthorized error with a generic error message.
     *
     * @param e the exception that was thrown (BadCredentialsException)
     * @return a {@link ResponseEntity} containing an {@link ApiErrorResponse} with HTTP status 401 (Unauthorized)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequestException(BadCredentialsException e) {
        log.error("BadCredentialsException occurred", e);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("Incorrect email or password")
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles entity not found exceptions, typically when a requested entity does not exist.
     * Logs the exception and returns a 404 Not Found error with the exception message.
     *
     * @param e the exception that was thrown (EntityNotFoundException)
     * @return a {@link ResponseEntity} containing an {@link ApiErrorResponse} with HTTP status 404 (Not Found)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException occurred", e);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles illegal argument exceptions thrown by the application.
     * Logs the exception and returns a 400 Bad Request with the exception message.
     *
     * @param e the exception that was thrown (IllegalArgumentException)
     * @return a {@link ResponseEntity} containing an {@link ApiErrorResponse} with HTTP status 400 (Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException occurred", e);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
