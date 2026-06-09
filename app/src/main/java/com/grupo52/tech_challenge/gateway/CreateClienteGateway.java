package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.exception.GatewayException;

public interface CreateClienteGateway {
    Cliente execute(Cliente cliente) throws GatewayException;
}