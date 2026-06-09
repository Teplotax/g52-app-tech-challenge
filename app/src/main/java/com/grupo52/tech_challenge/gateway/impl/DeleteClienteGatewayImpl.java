package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.DeleteClienteGateway;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteClienteGatewayImpl implements DeleteClienteGateway {

    private final ClienteRepository repository;

    @Override
    public void execute(Long clienteId) throws GatewayException {
        try {
            if (!repository.existsById(clienteId)) {
                throw new NotFoundGatewayException("Cliente não encontrado");
            }
            repository.deleteById(clienteId);
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao deletar Cliente, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}