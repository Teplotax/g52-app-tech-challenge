package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreateClienteGateway;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateClienteGatewayImpl implements CreateClienteGateway {

    private final ClienteRepository repository;

    public Cliente execute(Cliente cliente) throws GatewayException {
        try {
            return repository.save(ClienteDatabase.fromDomain(cliente)).toDomain();
        } catch (DataIntegrityViolationException e) {
            throw new GatewayException("Falha ao cadastrar Cliente, documento já cadastrado", 409);
        } catch (Exception e) {
            throw new GatewayException("Falha ao cadastrar Cliente, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}
