package com.grupo52.tech_challenge.usecase.impl;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.ClearTagChaveGateway;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.gateway.SendNotaFiscalEmailGateway;
import com.grupo52.tech_challenge.gateway.UpdateOrdemGateway;
import com.grupo52.tech_challenge.usecase.EntregarOrdemUseCase;
import com.grupo52.tech_challenge.usecase.UpdateOrdemStatusUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntregarOrdemUseCaseImpl implements EntregarOrdemUseCase {

    @Autowired
    private FindOrdemGateway findOrdemGateway;

    @Autowired
    private UpdateOrdemStatusUseCase updateOrdemStatusUseCase;

    @Autowired
    private UpdateOrdemGateway updateOrdemGateway;

    @Autowired
    private ClearTagChaveGateway clearTagChaveGateway;

    @Autowired
    private SendNotaFiscalEmailGateway sendNotaFiscalEmailGateway;

    @Override
    public Ordem execute(Long osId) throws GatewayException, ValidationException, UseCaseException {
        try {
            Ordem os = findOrdemGateway.execute(osId);

            switch (os.getStatus()) {
                case FINALIZADA -> {
                    updateOrdemStatusUseCase.execute(os, Status.ENTREGUE);
                    Ordem updated = updateOrdemGateway.execute(os);
                    clearTagChaveGateway.execute(osId);
                    sendNotaFiscalEmailGateway.execute(updated);
                    return updated;
                }
                case CANCELADA -> {
                    updateOrdemStatusUseCase.execute(os, Status.DEVOLVIDO);
                    Ordem updated = updateOrdemGateway.execute(os);
                    clearTagChaveGateway.execute(osId);
                    return updated;
                }
                default -> throw new InvalidStatusException(
                        "Entrega não permitida para OS no status '" + os.getStatus() + "'. Status esperado: FINALIZADA ou CANCELADA"
                );
            }
        } catch (ValidationException | GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new UseCaseException("Falha inesperada ao entregar OS id=" + osId + ": " + e.getLocalizedMessage(), e);
        }
    }
}