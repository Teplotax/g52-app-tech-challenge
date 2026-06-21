package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.request.AddServicosRequestDTO;
import com.grupo52.tech_challenge.dto.request.CreateOSRequestDTO;
import com.grupo52.tech_challenge.dto.response.*;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusChangeException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.CreateOSGateway;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.ListOSGateway;
import com.grupo52.tech_challenge.service.AddServicosService;
import com.grupo52.tech_challenge.service.CalculateOSPriceService;
import com.grupo52.tech_challenge.service.EvaluateOSService;
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
import java.time.LocalDate;

@RestController
@RequestMapping("/ordensDeServico")
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

    @Autowired
    private EvaluateOSService evaluateOSService;

    @Autowired
    private AddServicosService addServicosOSService;

    @PostMapping
    public ResponseEntity<CreateOSResponseDTO> createOS(
            @RequestBody @Valid CreateOSRequestDTO createOSRequestDTO) throws GatewayException {

        OrdemDeServico os = calculateOSPriceService
                .calculateServicosDesejados(createOSGateway.execute(createOSRequestDTO.toDomain()));

        return ResponseEntity.created(buildLocationUri(os)).body(CreateOSResponseDTO.fromDomain(os));
    }

    @PostMapping("/{ordemDeServicoId}/diagnosticar")
    public ResponseEntity<EvaluateOSResponseDTO> evaluate(
            @PathVariable Long ordemDeServicoId) throws GatewayException, ValidationException {

        OrdemDeServico os = evaluateOSService.execute(ordemDeServicoId);

        return ResponseEntity.ok(EvaluateOSResponseDTO.fromDomain(os));
    }

    @PostMapping("/{ordemDeServicoId}/adicionarServicos")
    public ResponseEntity<AddServicosResponseDTO> addServicos(
            @PathVariable Long ordemDeServicoId, @RequestBody @Valid AddServicosRequestDTO createOSRequestDTO) throws GatewayException, ValidationException {

        OrdemDeServico os = addServicosOSService.execute(createOSRequestDTO.toDomain(ordemDeServicoId));

        return ResponseEntity.ok(AddServicosResponseDTO.fromDomain(os));
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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
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