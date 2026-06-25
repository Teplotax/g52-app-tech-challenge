package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;

public interface CreateOSGateway {
    OrdemDeServico execute(OrdemDeServico os) throws GatewayException, ServiceException;
}