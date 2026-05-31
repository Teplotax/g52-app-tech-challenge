package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.FindClienteGateway;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindClienteGatewayImpl implements FindClienteGateway {

    private final ClienteRepository repository;

    public Cliente execute(Long clienteId) throws GatewayException {
        try {
            Optional<ClienteDatabase> clienteOptional = repository.findById(clienteId);
            ClienteDatabase clienteDatabase = clienteOptional.orElseThrow(
                    () -> new NotFoundGatewayException("Cliente não encontrado")
            );
            return clienteDatabase.toDomain();
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao consultar cliente, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}
