package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.FindServicoGateway;
import com.grupo52.tech_challenge.gateway.database.model.ServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindServicoGatewayImpl implements FindServicoGateway {

    private final ServicoRepository repository;

    @Override
    public Servico execute(Long servicoId) throws GatewayException {
        try {
            Optional<ServicoDatabase> optional = repository.findById(servicoId);
            ServicoDatabase servicoDatabase = optional.orElseThrow(
                    () -> new NotFoundGatewayException("Serviço não encontrado")
            );
            return servicoDatabase.toDomain();
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao consultar serviço, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}