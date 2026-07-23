package com.grupo52.tech_challenge.usecase;

import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;

public interface CalculateOrdemPriceUseCase {
    Ordem calculateServicosDesejados(Ordem os) throws GatewayException, UseCaseException;
    Ordem calculateServicosNecessarios(Ordem os) throws GatewayException, UseCaseException;
    Ordem calculateServicosAdicionais(Ordem os) throws GatewayException, UseCaseException;
    Ordem calculateApprovedPrice(Ordem os) throws GatewayException, UseCaseException;
}
