package com.grupo52.tech_challenge.usecase.impl;

import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.gateway.CreateOrdemGateway;
import com.grupo52.tech_challenge.usecase.CalculateOrdemPriceUseCase;
import com.grupo52.tech_challenge.usecase.CreateOrdemUseCase;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateOrdemUseCaseImpl implements CreateOrdemUseCase {

    @Autowired
    private CreateOrdemGateway createOrdemGateway;

    @Autowired
    private CalculateOrdemPriceUseCase calculateOrdemPriceUseCase;

    @Override
    @Transactional
    public Ordem execute(Ordem os) throws GatewayException, UseCaseException {

        try {
            os = createOrdemGateway.execute(os);

            return calculateOrdemPriceUseCase.calculateServicosDesejados(os);
        } catch (GatewayException | UseCaseException e) {
            throw e;
        } catch (Exception e) {
            throw new UseCaseException("Falha inesperada ao criar OS", e);
        }
    }
}