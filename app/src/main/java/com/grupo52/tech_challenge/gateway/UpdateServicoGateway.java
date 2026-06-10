package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;

public interface UpdateServicoGateway {
    Servico execute(Servico servico) throws GatewayException;
}