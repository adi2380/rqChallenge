package com.reliaquest.api.controller;

import com.reliaquest.api.exceptions.ResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Slf4j
@ControllerAdvice
public class EmployeeControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<?> handleException(IllegalArgumentException ex) {
        log.error("Bad input request", ex);
        return ResponseEntity.status(400).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<?> handleException(ResourceNotFoundException ex) {
        log.error("Resource not found", ex);
        return ResponseEntity.status(404).body("Requested resource not found.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Map<String, String>> handleException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    protected ResponseEntity<?> handleException(HttpServerErrorException ex) {
        log.error("Validation failed for create request input", ex);
        return ResponseEntity.status(400)
                .body(
                        "Validation failed for create request input, one for more field has incorrect value, please check the request body , cause:  "
                                + ex.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    protected ResponseEntity<?> handleException(HttpClientErrorException ex) {
        if (ex.getStatusCode().value() == 400) {
            log.error("Invalid Data input provided, cause :{}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode())
                    .body("Invalid Data input provided, cause : " + ex.getMessage());
        }

        if (ex.getStatusCode().value() == 429) {
            log.error("Too many request received to process");
            return ResponseEntity.status(ex.getStatusCode()).body("Too many request received to process");
        }
        log.error("Resource not found", ex);
        return ResponseEntity.status(ex.getStatusCode()).body("Requested resource not found.");
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleException(Exception ex) {
        log.error("Resource not found", ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}
