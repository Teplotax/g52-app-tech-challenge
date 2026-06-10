package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.DeleteServicoGateway;
import com.grupo52.tech_challenge.gateway.database.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteServicoGatewayImpl implements DeleteServicoGateway {

    private final ServicoRepository repository;

    @Override
    public void execute(Long servicoId) throws GatewayException {
        try {
            if (!repository.existsById(servicoId)) {
                throw new NotFoundGatewayException("Serviço não encontrado");
            }
            repository.deleteById(servicoId);
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao deletar Serviço, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}