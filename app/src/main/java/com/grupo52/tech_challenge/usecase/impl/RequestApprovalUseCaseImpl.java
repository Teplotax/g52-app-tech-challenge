package com.grupo52.tech_challenge.usecase.impl;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.gateway.SendOrcamentoEmailGateway;
import com.grupo52.tech_challenge.gateway.UpdateOrdemGateway;
import com.grupo52.tech_challenge.usecase.RequestApprovalUseCase;
import com.grupo52.tech_challenge.usecase.UpdateOrdemStatusUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestApprovalUseCaseImpl implements RequestApprovalUseCase {

    @Autowired
    private FindOrdemGateway findOrdemGateway;

    @Autowired
    private UpdateOrdemGateway updateOrdemGateway;

    @Autowired
    private UpdateOrdemStatusUseCase updateOrdemStatusUseCase;

    @Autowired
    private SendOrcamentoEmailGateway sendOrcamentoEmailGateway;

    @Override
    public Ordem execute(Long osId) throws GatewayException, ValidationException, UseCaseException {
        try {
            Ordem os = findOrdemGateway.execute(osId);
            updateOrdemStatusUseCase.execute(os, Status.AGUARDANDO_APROVACAO);
            Ordem updated = updateOrdemGateway.execute(os);
            sendOrcamentoEmailGateway.execute(updated);
            return updated;
        } catch (ValidationException | GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new UseCaseException("Falha inesperada ao solicitar aprovação da OS", e);
        }
    }
}
