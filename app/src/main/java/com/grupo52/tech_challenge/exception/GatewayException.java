package com.grupo52.tech_challenge.exception;

import lombok.Getter;

@Getter
public class GatewayException  extends Exception {
    private int status = 502;

    public GatewayException(String message) { super(message); }

    public GatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatewayException(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
    }

    public GatewayException(String message, int status) {
        super(message);
        this.status = status;
    }
}
