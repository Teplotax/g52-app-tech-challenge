package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.FindInsumoByVeiculoGateway;
import com.grupo52.tech_challenge.gateway.FindInsumoByVeiculoGateway;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindInsumoByVeiculoGatewayImpl implements FindInsumoByVeiculoGateway {

    private final ProdutoRepository repository;

    public List<Insumo> execute(TipoInsumo tipoInsumo, Veiculo veiculo) throws GatewayException {
        try {
            List<ProdutoDatabase> pecas = repository.findAllByTipoInsumoAndAplicacoesModeloIdAndAplicacoesAnoInicioLessThanEqualAndAplicacoesAnoFimGreaterThanEqual(tipoInsumo, veiculo.getModelo().getId(), veiculo.getAno(), veiculo.getAno());


            return pecas.stream().map(ProdutoDatabase::toInsumoDomain).toList();

        } catch (Exception e) {
            throw new GatewayException("Falha ao consultar peças, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}