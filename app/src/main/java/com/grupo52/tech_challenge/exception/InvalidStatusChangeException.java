package com.grupo52.tech_challenge.exception;

import lombok.Getter;

@Getter
public class InvalidStatusChangeException extends Exception {
    private int status = 422;

    public InvalidStatusChangeException(String message) { super(message); }

    public InvalidStatusChangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidStatusChangeException(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
    }

    public InvalidStatusChangeException(String message, int status) {
        super(message);
        this.status = status;
    }
}
