package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;

public interface UpdateVeiculoGateway {
    Veiculo execute(Veiculo veiculo) throws GatewayException;
}
 