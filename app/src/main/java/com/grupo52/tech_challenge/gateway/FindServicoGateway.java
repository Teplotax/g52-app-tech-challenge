package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;

public interface FindServicoGateway {
    Servico execute(Long servicoId) throws GatewayException;
}