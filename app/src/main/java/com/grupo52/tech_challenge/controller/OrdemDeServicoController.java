package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.request.CreateOSRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateOSResponseDTO;
import com.grupo52.tech_challenge.dto.response.FindOSResponseDTO;
import com.grupo52.tech_challenge.dto.response.OSInfoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreateOSGateway;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.ListOSGateway;
import com.grupo52.tech_challenge.service.CalculateOSPriceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/ordemsDeServico")
@Validated
public class OrdemDeServicoController {

    @Autowired
    private CreateOSGateway createOSGateway;

    @Autowired
    private CalculateOSPriceService calculateOSPriceService;

    @Autowired
    private FindOSGateway findOSGateway;

    @Autowired
    private ListOSGateway listOSGateway;

    @PostMapping
    public ResponseEntity<CreateOSResponseDTO> createOS(
            @RequestBody @Valid CreateOSRequestDTO createOSRequestDTO) throws GatewayException {

        OrdemDeServico os = calculateOSPriceService
                .calculateServicosDesejados(createOSGateway.execute(createOSRequestDTO.toDomain()));

        return ResponseEntity.created(buildLocationUri(os)).body(CreateOSResponseDTO.fromDomain(os));
    }

    @GetMapping("/{ordemDeServicoId}")
    public ResponseEntity<FindOSResponseDTO> findOS(
            @PathVariable Long ordemDeServicoId) throws GatewayException {

        OrdemDeServico os = findOSGateway.execute(ordemDeServicoId);
        return ResponseEntity.ok(FindOSResponseDTO.fromDomain(os));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<OSInfoResponseDTO>> listOSs(
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String documentoCliente,
            @RequestParam(required = false) StatusOS status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @PageableDefault(size = 20, page = 0) Pageable pageable) throws GatewayException {

        Page<OrdemDeServico> ordemDeServicos = listOSGateway.execute(
                placa, documentoCliente, status, dataInicio, dataFim, pageable);

        return ResponseEntity.ok(OSInfoResponseDTO.fromDomain(ordemDeServicos));
    }

    private URI buildLocationUri(OrdemDeServico os) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(os.getId())
                .toUri();
    }
}