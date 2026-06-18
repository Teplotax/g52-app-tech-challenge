package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;

public interface CalculateOSPriceService {
    OrdemDeServico calculateServicosDesejados(OrdemDeServico os) throws GatewayException;
}
