package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;

public interface OrcamentoDocumentGateway {
    String buildHtml(OrdemDeServico os);
    byte[] buildPdf(OrdemDeServico os) throws GatewayException;
}
