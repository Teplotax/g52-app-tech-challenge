package com.grupo52.tech_challenge.exception;

import lombok.Getter;

@Getter
public class NotFoundGatewayException extends GatewayException{
    private static final int status = 404;


    public NotFoundGatewayException(String message, Throwable cause) {
        super(message, cause, status);
    }

    public NotFoundGatewayException(String message) {
        super(message, status);
    }
}
