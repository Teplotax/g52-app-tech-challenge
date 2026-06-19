package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ListOSGateway;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoSpecification;
import com.grupo52.tech_challenge.gateway.database.repository.OrdemDeServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class ListOSGatewayImpl implements ListOSGateway {

    private final OrdemDeServicoRepository repository;

    @Override
    public Page<OrdemDeServico> execute(
            String placa,
            String documentoCliente,
            StatusOS status,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    ) throws GatewayException {
        try {
            Specification<OrdemDeServicoDatabase> spec = OrdemDeServicoSpecification.withFilters(
                    placa, documentoCliente, status,
                    dataInicio != null ? dataInicio.atStartOfDay() : null,
                    dataFim != null ? dataFim.atTime(LocalTime.MAX) : null
            );
            return repository.findAll(spec, pageable).map(OrdemDeServicoDatabase::toDomain);
        } catch (Exception e) {
            throw new GatewayException("Falha ao listar ordens de serviço, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}