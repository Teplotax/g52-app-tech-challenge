package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;

public interface OrcamentoDocumentGateway {
    String buildHtml(Ordem os);
    byte[] buildPdf(Ordem os) throws GatewayException;
}
