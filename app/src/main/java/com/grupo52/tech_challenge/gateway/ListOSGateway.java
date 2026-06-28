package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.ComplexidadeOS;
import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ListOSGateway {
    Page<OrdemDeServico> execute(
            String placa,
            String documentoCliente,
            StatusOS status,
            ComplexidadeOS complexidade,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    ) throws GatewayException;
}