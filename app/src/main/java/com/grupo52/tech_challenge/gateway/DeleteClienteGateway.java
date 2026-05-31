package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.exception.GatewayException;

public interface DeleteClienteGateway {
    void execute(Long clienteId) throws GatewayException;
}