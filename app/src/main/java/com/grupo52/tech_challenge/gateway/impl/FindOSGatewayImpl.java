package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.OrdemDeServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindOSGatewayImpl implements FindOSGateway {

    private final OrdemDeServicoRepository repository;

    @Override
    @Transactional(readOnly = true)
    public OrdemDeServico execute(Long osId) throws GatewayException {
        try {
            Optional<OrdemDeServicoDatabase> osOptional = repository.findById(osId);
            OrdemDeServicoDatabase osDatabase = osOptional.orElseThrow(
                    () -> new NotFoundGatewayException("Ordem de Serviço não encontrada")
            );
            return osDatabase.toDomain();
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao consultar OS, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}