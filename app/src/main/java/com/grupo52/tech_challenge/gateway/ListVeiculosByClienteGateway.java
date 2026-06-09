package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;

import java.util.List;

public interface ListVeiculosByClienteGateway {
    List<Veiculo> execute(Long clienteId) throws GatewayException;
}
