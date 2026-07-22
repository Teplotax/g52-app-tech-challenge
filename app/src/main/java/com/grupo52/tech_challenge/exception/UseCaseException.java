package com.grupo52.tech_challenge.exception;

import lombok.Getter;

@Getter
public class UseCaseException extends Exception {
    private int status = 500;

    public UseCaseException(String message) { super(message); }

    public UseCaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public UseCaseException(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
    }

    public UseCaseException(String message, int status) {
        super(message);
        this.status = status;
    }
}
