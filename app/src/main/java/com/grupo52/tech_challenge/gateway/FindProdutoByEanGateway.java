package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Produto;
import com.grupo52.tech_challenge.exception.GatewayException;

public interface FindProdutoByEanGateway {
    Produto execute(String ean) throws GatewayException;
}