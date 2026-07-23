package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Enums.Complexidade;
import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ListOrdemGateway;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoSpecification;
import com.grupo52.tech_challenge.gateway.database.repository.OrdemDeServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class ListOrdemGatewayImpl implements ListOrdemGateway {

    private final OrdemDeServicoRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Page<Ordem> execute(
            String placa,
            String documentoCliente,
            Status status,
            Complexidade complexidade,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    ) throws GatewayException {
        try {
            Specification<OrdemDeServicoDatabase> spec = OrdemDeServicoSpecification.withFilters(
                    placa, documentoCliente, status, complexidade,
                    dataInicio != null ? dataInicio.atStartOfDay() : null,
                    dataFim != null ? dataFim.atTime(LocalTime.MAX) : null
            );
            return repository.findAll(spec, pageable).map(OrdemDeServicoDatabase::toDomain);
        } catch (Exception e) {
            throw new GatewayException("Falha ao listar ordens de serviço, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}