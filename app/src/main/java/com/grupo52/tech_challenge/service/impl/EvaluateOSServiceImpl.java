package com.grupo52.tech_challenge.service.impl;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusChangeException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.UpdateOSGateway;
import com.grupo52.tech_challenge.service.EvaluateOSService;
import com.grupo52.tech_challenge.service.UpdateOSStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvaluateOSServiceImpl implements EvaluateOSService {


    @Autowired
    private FindOSGateway findOSGateway;

    @Autowired
    private UpdateOSGateway updateOSGateway;

    @Autowired
    private UpdateOSStatusService updateOSStatusService;

    @Override
    public OrdemDeServico execute(Long osId) throws GatewayException, ValidationException {

        try {
           OrdemDeServico os = findOSGateway.execute(osId);
            updateOSStatusService.execute(os, StatusOS.EM_DIAGNOSTICO);

            return updateOSGateway.execute(os);

        } catch (ValidationException | GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException(e.getClass().getSimpleName());
        }
    }
}