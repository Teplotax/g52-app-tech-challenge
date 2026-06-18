package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;

import java.util.List;

public interface FindPecaByVeiculoGateway {
    List<Peca> execute(TipoPeca tipoPeca, Veiculo veiculo) throws GatewayException;
}