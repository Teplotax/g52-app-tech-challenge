package com.grupo52.tech_challenge.exception;

import lombok.Getter;

@Getter
public class ValidationException extends Exception {
    private int status = 422;

    public ValidationException(String message) { super(message); }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
    }

    public ValidationException(String message, int status) {
        super(message);
        this.status = status;
    }
}
