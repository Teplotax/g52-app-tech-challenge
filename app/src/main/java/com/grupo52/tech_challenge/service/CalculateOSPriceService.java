package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;

public interface CalculateOSPriceService {
    OrdemDeServico calculateServicosDesejados(OrdemDeServico os) throws GatewayException, ServiceException;
    OrdemDeServico calculateServicosNecessarios(OrdemDeServico os) throws GatewayException, ServiceException;
    OrdemDeServico calculateServicosAdicionais(OrdemDeServico os) throws GatewayException, ServiceException;
    OrdemDeServico calculateApprovedPrice(OrdemDeServico os) throws GatewayException, ServiceException;
}
