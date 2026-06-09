package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.DeleteProdutoGateway;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteProdutoGatewayImpl implements DeleteProdutoGateway {

    private final ProdutoRepository repository;

    @Override
    public void execute(Long produtoId, TipoProduto tipoProduto) throws GatewayException {
        try {
            if (!repository.existsByIdAndTipoProduto(produtoId, tipoProduto)) {
                throw new NotFoundGatewayException("Produto não encontrado");
            }
            repository.deleteById(produtoId);
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao deletar Produto, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}