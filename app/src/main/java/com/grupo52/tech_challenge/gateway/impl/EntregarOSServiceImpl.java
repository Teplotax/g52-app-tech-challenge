package com.grupo52.tech_challenge.service.impl;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.ClearTagChaveGateway;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.SendNotaFiscalEmailGateway;
import com.grupo52.tech_challenge.gateway.UpdateOSGateway;
import com.grupo52.tech_challenge.service.EntregarOSService;
import com.grupo52.tech_challenge.service.UpdateOSStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntregarOSServiceImpl implements EntregarOSService {

    @Autowired
    private FindOSGateway findOSGateway;

    @Autowired
    private UpdateOSStatusService updateOSStatusService;

    @Autowired
    private UpdateOSGateway updateOSGateway;

    @Autowired
    private ClearTagChaveGateway clearTagChaveGateway;

    @Autowired
    private SendNotaFiscalEmailGateway sendNotaFiscalEmailGateway;

    @Override
    public OrdemDeServico execute(Long osId) throws GatewayException, ValidationException, ServiceException {
        try {
            OrdemDeServico os = findOSGateway.execute(osId);

            switch (os.getStatus()) {
                case FINALIZADA -> {
                    updateOSStatusService.execute(os, StatusOS.ENTREGUE);
                    OrdemDeServico updated = updateOSGateway.execute(os);
                    clearTagChaveGateway.execute(osId);
                    sendNotaFiscalEmailGateway.execute(updated);
                    return updated;
                }
                case CANCELADA -> {
                    updateOSStatusService.execute(os, StatusOS.DEVOLVIDO);
                    OrdemDeServico updated = updateOSGateway.execute(os);
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
            throw new ServiceException("Falha inesperada ao entregar OS id=" + osId + ": " + e.getLocalizedMessage(), e);
        }
    }
}