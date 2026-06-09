package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.exception.GatewayException;

import java.util.List;

public interface ListModelosByMarcaGateway {
    List<Modelo> execute(Long marcaId) throws GatewayException;
}