package com.grupo52.tech_challenge.service.impl;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.SendOrcamentoEmailGateway;
import com.grupo52.tech_challenge.gateway.UpdateOSGateway;
import com.grupo52.tech_challenge.service.RequestApprovalService;
import com.grupo52.tech_challenge.service.UpdateOSStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestApprovalServiceImpl implements RequestApprovalService {

    @Autowired
    private FindOSGateway findOSGateway;

    @Autowired
    private UpdateOSGateway updateOSGateway;

    @Autowired
    private UpdateOSStatusService updateOSStatusService;

    @Autowired
    private SendOrcamentoEmailGateway sendOrcamentoEmailGateway;

    @Override
    public OrdemDeServico execute(Long osId) throws GatewayException, ValidationException, ServiceException {
        try {
            OrdemDeServico os = findOSGateway.execute(osId);
            updateOSStatusService.execute(os, StatusOS.AGUARDANDO_APROVACAO);
            OrdemDeServico updated = updateOSGateway.execute(os);
            sendOrcamentoEmailGateway.execute(updated);
            return updated;
        } catch (ValidationException | GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada ao solicitar aprovação da OS", e);
        }
    }
}
