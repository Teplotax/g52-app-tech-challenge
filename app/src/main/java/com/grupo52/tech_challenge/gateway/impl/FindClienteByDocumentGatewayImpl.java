package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.FindClienteByDocumentGateway;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindClienteByDocumentGatewayImpl implements FindClienteByDocumentGateway {

    private final ClienteRepository repository;

    public Cliente execute(String documento) throws GatewayException {
        try {
            Optional<ClienteDatabase> clienteOptional = repository.findByDocumento(documento);
            ClienteDatabase clienteDatabase = clienteOptional.orElseThrow(
                    () -> new NotFoundGatewayException("Cliente não encontrado")
            );
            return clienteDatabase.toDomain();
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao consultar cliente pelo documento, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}
