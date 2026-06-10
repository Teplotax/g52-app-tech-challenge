package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.exception.GatewayException;

public interface DeleteServicoGateway {
    void execute(Long servicoId) throws GatewayException;
}