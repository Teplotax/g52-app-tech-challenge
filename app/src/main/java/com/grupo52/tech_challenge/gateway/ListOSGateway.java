package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ListOSGateway {
    Page<OrdemDeServico> execute(
            String placa,
            String documentoCliente,
            StatusOS status,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Pageable pageable
    ) throws GatewayException;
}