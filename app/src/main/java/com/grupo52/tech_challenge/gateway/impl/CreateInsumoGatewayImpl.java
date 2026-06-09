package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreateInsumoGateway;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateInsumoGatewayImpl implements CreateInsumoGateway {

    private final ProdutoRepository repository;

    public Insumo execute(Insumo insumo) throws GatewayException {
        try {
            return repository.save(ProdutoDatabase.fromDomain(insumo)).toInsumoDomain();
        } catch (DataIntegrityViolationException e) {
            throw new GatewayException("Falha ao cadastrar Insumo", 409);
        } catch (Exception e) {
            throw new GatewayException("Falha ao cadastrar Insumo, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}
