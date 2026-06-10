package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreateServicoGateway;
import com.grupo52.tech_challenge.gateway.database.model.ServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateServicoGatewayImpl implements CreateServicoGateway {

    private final ServicoRepository repository;

    public Servico execute(Servico servico) throws GatewayException {
        try {
            return repository.save(ServicoDatabase.fromDomain(servico)).toDomain();
        } catch (DataIntegrityViolationException e) {
            throw new GatewayException("Falha ao cadastrar Serviço", 409);
        } catch (Exception e) {
            throw new GatewayException("Falha ao cadastrar Serviço, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}
