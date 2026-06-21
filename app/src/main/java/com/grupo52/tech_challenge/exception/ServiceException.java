package com.grupo52.tech_challenge.exception;

import lombok.Getter;

@Getter
public class ServiceException extends Exception {
    private int status = 500;

    public ServiceException(String message) { super(message); }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
    }

    public ServiceException(String message, int status) {
        super(message);
        this.status = status;
    }
}
