package com.server.calendar.util.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import com.server.calendar.util.response.CustomApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomApiResponse<?>> handleMethodArgumentNotValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomApiResponse.createFailWithoutData(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomApiResponse<?>> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomApiResponse.createFailWithoutData(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    // Custom Exception
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CustomApiResponse<?>> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CustomApiResponse.createFailWithoutData(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(EntityDuplicatedException.class)
    public ResponseEntity<CustomApiResponse<?>> handleEntityDuplicatedException(EntityDuplicatedException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(CustomApiResponse.createFailWithoutData(HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<CustomApiResponse<?>> handleCustomValidationException(CustomValidationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomApiResponse.createFailWithoutData(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(PasswordIncorrectException.class)
    public ResponseEntity<CustomApiResponse<?>> handlePasswordIncorrectException(PasswordIncorrectException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomApiResponse.createFailWithoutData(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(PasswordNotChangedException.class)
    public ResponseEntity<CustomApiResponse<?>> handlePasswordNotChangedException(PasswordNotChangedException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomApiResponse.createFailWithoutData(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }


}
