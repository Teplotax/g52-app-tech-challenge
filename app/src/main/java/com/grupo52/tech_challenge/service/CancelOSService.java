package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;

public interface CancelOSService {
    OrdemDeServico execute(Long osId) throws GatewayException, ServiceException, ValidationException;
}