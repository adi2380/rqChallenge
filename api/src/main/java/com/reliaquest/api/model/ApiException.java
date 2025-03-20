package com.reliaquest.api.model;

public class ApiException extends RuntimeException {

    int status;

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, int statusCode) {
        super(message);
        this.status = statusCode;
    }

    public int getStatus() {
        return status;
    }
}
