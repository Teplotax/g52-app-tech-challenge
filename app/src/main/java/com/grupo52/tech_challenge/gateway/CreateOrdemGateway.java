package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;

public interface CreateOrdemGateway {
    Ordem execute(Ordem os) throws GatewayException, UseCaseException;
}