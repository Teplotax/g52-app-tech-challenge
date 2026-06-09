package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.FindPecaGateway;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindPecaGatewayImpl implements FindPecaGateway {

    private final ProdutoRepository repository;

    public Peca execute(Long clienteId) throws GatewayException {
        try {
            Optional<ProdutoDatabase> pecaOptional = repository.findById(clienteId);
            ProdutoDatabase pecaDatabase = pecaOptional.orElseThrow(
                    () -> new NotFoundGatewayException("Peça não encontrada")
            );
            return pecaDatabase.toPecaDomain();
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao consultar peça, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}
