package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusChangeException;

public interface EvaluateOSService {
    OrdemDeServico execute(Long osId) throws GatewayException, InvalidStatusChangeException;
}
