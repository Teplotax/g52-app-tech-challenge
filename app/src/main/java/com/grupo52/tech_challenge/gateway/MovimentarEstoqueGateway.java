package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Produto;
import com.grupo52.tech_challenge.exception.GatewayException;

import java.util.List;

public interface MovimentarEstoqueGateway {
    List<Produto> entrada(List<String> eans, List<Integer> quantidades) throws GatewayException;
    List<Produto> saida(List<String> eans, List<Integer> quantidades) throws GatewayException;
}