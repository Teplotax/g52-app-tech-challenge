package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.exception.GatewayException;

public interface FindInsumoGateway {
    Insumo execute(Long pecaId) throws GatewayException;
}