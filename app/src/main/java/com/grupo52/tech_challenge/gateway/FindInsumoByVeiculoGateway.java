package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;

import java.util.List;

public interface FindInsumoByVeiculoGateway {
    List<Insumo> execute(TipoInsumo tipoInsumo, Veiculo veiculo) throws GatewayException;
}