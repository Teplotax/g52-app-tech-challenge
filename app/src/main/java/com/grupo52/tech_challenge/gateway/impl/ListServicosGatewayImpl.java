package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ListServicosGateway;
import com.grupo52.tech_challenge.gateway.database.model.ServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListServicosGatewayImpl implements ListServicosGateway {

    private final ServicoRepository repository;

    @Override
    public Page<Servico> execute(Pageable pageable) throws GatewayException {
        try {
            return repository.findAll(pageable).map(ServicoDatabase::toDomain);
        } catch (Exception e) {
            throw new GatewayException("Falha ao listar serviços", e);
        }
    }
}