package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreatePecaGateway;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.AplicacaoProdutoRepository;
import com.grupo52.tech_challenge.gateway.database.repository.ModeloRepository;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePecaGatewayImpl implements CreatePecaGateway {

    private final ProdutoRepository produtoRepository;

    private final ModeloRepository modeloRepository;

    private final AplicacaoProdutoRepository aplicacaoProdutoRepository;


    public Peca execute(Peca peca) throws GatewayException {
        try {
            ProdutoDatabase produtoDatabase = produtoRepository.save(ProdutoDatabase.fromDomain(peca));

            return produtoDatabase.toPecaDomain();
        } catch (DataIntegrityViolationException e) {
            throw new GatewayException("Falha ao cadastrar Peça, sku e ean devem ser únicos", 409);
        } catch (Exception e) {
            throw new GatewayException("Falha ao cadastrar Peça, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}
