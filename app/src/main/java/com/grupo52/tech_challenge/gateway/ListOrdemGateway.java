package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.Complexidade;
import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ListOrdemGateway {
    Page<Ordem> execute(
            String placa,
            String documentoCliente,
            Status status,
            Complexidade complexidade,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    ) throws GatewayException;
}