package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ListVeiculosGateway;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListVeiculosGatewayImpl implements ListVeiculosGateway {

    private final VeiculoRepository repository;

    public Page<Veiculo> execute(Pageable pageable) throws GatewayException {
        try {
            return repository.findAll(pageable).map(VeiculoDatabase::toDomain);
        } catch (Exception e) {
            throw new GatewayException("Falha ao listar veículos", e);
        }
    }
}
