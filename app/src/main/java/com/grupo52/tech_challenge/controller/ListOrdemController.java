package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.ListOrdemApi;
import com.grupo52.tech_challenge.domain.Enums.Complexidade;
import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.response.OSInfoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ListOrdemGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class ListOrdemController implements ListOrdemApi {

    @Autowired
    private ListOrdemGateway listOrdemGateway;

    @Override
    public ResponseEntity<PagedResponse<OSInfoResponseDTO>> execute(
            String placa,
            String documentoCliente,
            Status status,
            Complexidade complexidade,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable) throws GatewayException {

        Page<Ordem> ordemDeServicos = listOrdemGateway.execute(
                placa, documentoCliente, status, complexidade, dataInicio, dataFim, pageable);

        return ResponseEntity.ok(OSInfoResponseDTO.fromDomain(ordemDeServicos));
    }
}