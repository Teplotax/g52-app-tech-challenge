package com.grupo52.tech_challenge.api;

import com.grupo52.tech_challenge.domain.Enums.Complexidade;
import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.response.OSInfoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

public interface ListOrdemApi {

    @GetMapping("/ordensDeServico")
    ResponseEntity<PagedResponse<OSInfoResponseDTO>> execute(
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String documentoCliente,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Complexidade complexidade,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @PageableDefault(size = 20, page = 0) Pageable pageable) throws GatewayException;
}