package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.DeleteVeiculoGateway;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteVeiculoGatewayImpl implements DeleteVeiculoGateway {

    private final VeiculoRepository repository;

    @Override
    public void execute(Long veiculoId) throws GatewayException {
        try {
            if (!repository.existsById(veiculoId)) {
                throw new NotFoundGatewayException("Veículo não encontrado");
            }
            repository.deleteById(veiculoId);
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao deletar Veículo, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}