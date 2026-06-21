package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ValidationException;

public interface AddServicosService {
    OrdemDeServico execute(OrdemDeServico os) throws GatewayException, ValidationException;
}
