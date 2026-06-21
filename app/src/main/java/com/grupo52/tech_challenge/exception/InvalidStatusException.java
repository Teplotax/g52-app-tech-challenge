package com.grupo52.tech_challenge.exception;

import lombok.Getter;

@Getter
public class InvalidStatusException extends ValidationException {
    private static final int status = 422;

    public InvalidStatusException(String message, Throwable cause) {
        super(message, cause, status);
    }

    public InvalidStatusException(String message) {
        super(message, status);
    }
}
