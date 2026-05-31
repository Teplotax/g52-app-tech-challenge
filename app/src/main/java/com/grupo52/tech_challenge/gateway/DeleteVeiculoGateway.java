package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.exception.GatewayException;

public interface DeleteVeiculoGateway {
    void execute(Long veiculoId) throws GatewayException;
}