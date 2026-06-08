package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreatePecaGateway;
import com.grupo52.tech_challenge.gateway.database.model.AplicacaoProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.AplicacaoProdutoRepository;
import com.grupo52.tech_challenge.gateway.database.repository.ModeloRepository;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreatePecaGatewayImpl implements CreatePecaGateway {

    private final ProdutoRepository produtoRepository;

    private final ModeloRepository modeloRepository;

    private final AplicacaoProdutoRepository aplicacaoProdutoRepository;

    @Transactional
    public Peca execute(Peca peca) throws GatewayException {
        try {
            ProdutoDatabase produtoDatabase = produtoRepository.save(ProdutoDatabase.fromDomain(peca));

            List<AplicacaoProdutoDatabase> aplicacoes = peca.getAplicacoes().stream()
                    .map(aplicacao -> AplicacaoProdutoDatabase.builder()
                            .produto(produtoDatabase)  // already in memory, no proxy needed
                            .modelo(modeloRepository.getReferenceById(aplicacao.getModelo().getId()))
                            .quantidade(aplicacao.getQuantidade())
                            .anoInicio(aplicacao.getAnoInicio())
                            .anoFim(aplicacao.getAnoFim())
                            .build())
                    .toList();


            List<AplicacaoProdutoDatabase> savedAplicacoes = aplicacaoProdutoRepository.saveAll(aplicacoes);

            produtoDatabase.setAplicacoes(savedAplicacoes);

            return produtoDatabase.toPecaDomain();
        } catch (DataIntegrityViolationException e) {
            throw new GatewayException("Falha ao cadastrar Peça", 409);
        } catch (Exception e) {
            throw new GatewayException("Falha ao cadastrar Peça, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}
