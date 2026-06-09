package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.exception.GatewayException;

public interface DeleteProdutoGateway {
    void execute(Long produtoId, TipoProduto tipoProduto) throws GatewayException;
}