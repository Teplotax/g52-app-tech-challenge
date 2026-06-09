package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.exception.GatewayException;

public interface UpdatePecaGateway {
    Peca execute(Peca peca) throws GatewayException;
}
