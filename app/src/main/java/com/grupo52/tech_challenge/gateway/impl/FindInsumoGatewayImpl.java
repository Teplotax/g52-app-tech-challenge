package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.FindInsumoGateway;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindInsumoGatewayImpl implements FindInsumoGateway {

    private final ProdutoRepository repository;

    public Insumo execute(Long insumoId) throws GatewayException {
        try {
            Optional<ProdutoDatabase> pecaOptional = repository.findByIdAndTipoProduto(insumoId, TipoProduto.INSUMO);
            ProdutoDatabase insumoDatabase = pecaOptional.orElseThrow(
                    () -> new NotFoundGatewayException("Insumo não encontrada")
            );
            return insumoDatabase.toInsumoDomain();
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao consultar insumo, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}