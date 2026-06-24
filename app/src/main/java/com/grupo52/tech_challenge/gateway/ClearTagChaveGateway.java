package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.exception.GatewayException;

public interface ClearTagChaveGateway {
    void execute(Long osId) throws GatewayException;
}