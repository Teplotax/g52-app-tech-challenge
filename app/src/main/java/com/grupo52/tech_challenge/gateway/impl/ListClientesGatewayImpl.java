package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ListClientesGateway;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListClientesGatewayImpl implements ListClientesGateway {

    private final ClienteRepository repository;

    public Page<Cliente> execute(Pageable pageable) throws GatewayException {
        try {
            return repository.findAll(pageable).map(ClienteDatabase::toDomain);
        } catch (Exception e) {
            throw new GatewayException("Falha ao listar clientes", e);
        }
    }
}
