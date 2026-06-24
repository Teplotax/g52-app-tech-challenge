package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ClearTagChaveGateway;
import com.grupo52.tech_challenge.gateway.database.repository.OrdemDeServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ClearTagChaveGatewayImpl implements ClearTagChaveGateway {

    private final OrdemDeServicoRepository ordemDeServicoRepository;

    @Override
    @Transactional
    public void execute(Long osId) throws GatewayException {
        int updated = ordemDeServicoRepository.clearTagChave(osId);
        if (updated == 0) {
            throw new GatewayException("OrdemDeServico não encontrada ao limpar tagChave: id=" + osId);
        }
    }
}