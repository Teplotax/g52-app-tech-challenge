package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Produto;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.FindProdutoByEanGateway;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindProdutoByEanGatewayImpl implements FindProdutoByEanGateway {

    private final ProdutoRepository repository;

    @Override
    public Produto execute(String ean) throws GatewayException {
        ProdutoDatabase produto = repository.findByEan(ean)
                .orElseThrow(() -> new NotFoundGatewayException("Produto não encontrado para EAN: " + ean));

        return switch (produto.getTipoProduto()) {
            case PECA -> produto.toPecaDomain();
            case INSUMO -> produto.toInsumoDomain();
        };
    }
}