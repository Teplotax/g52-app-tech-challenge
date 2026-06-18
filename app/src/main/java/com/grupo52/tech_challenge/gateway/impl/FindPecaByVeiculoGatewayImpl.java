package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.FindPecaByVeiculoGateway;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindPecaByVeiculoGatewayImpl implements FindPecaByVeiculoGateway {

    private final ProdutoRepository repository;

    public List<Peca> execute(TipoPeca tipoPeca, Veiculo veiculo) throws GatewayException {
        try {
            List<ProdutoDatabase> pecas = repository.findAllByTipoPecaAndAplicacoesModeloIdAndAplicacoesAnoInicioLessThanEqualAndAplicacoesAnoFimGreaterThanEqual(tipoPeca, veiculo.getModelo().getId(), veiculo.getAno(), veiculo.getAno());


            return pecas.stream().map(ProdutoDatabase::toPecaDomain).toList();

        } catch (Exception e) {
            throw new GatewayException("Falha ao consultar peças, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}