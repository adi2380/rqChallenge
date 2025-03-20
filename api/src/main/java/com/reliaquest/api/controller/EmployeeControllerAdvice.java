package com.reliaquest.api.controller;

import com.reliaquest.api.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

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
