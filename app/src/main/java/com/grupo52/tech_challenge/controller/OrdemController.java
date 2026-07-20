package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Enums.Complexidade;
import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.request.AddServicosRequestDTO;
import com.grupo52.tech_challenge.dto.request.AprovarOSRequestDTO;
import com.grupo52.tech_challenge.dto.request.CreateOrderRequestDTO;
import com.grupo52.tech_challenge.dto.response.*;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.gateway.ListOrdemGateway;
import com.grupo52.tech_challenge.usecase.*;
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
public class OrdemController {

    @Autowired
    private CreateOrdemUseCase createOrdemUseCase;

    @Autowired
    private FindOrdemGateway findOrdemGateway;

    @Autowired
    private ListOrdemGateway listOrdemGateway;

    @Autowired
    private EvaluateOrdemUseCase evaluateOrdemUseCase;

    @Autowired
    private AddServicosUseCase addServicosOSService;

    @Autowired
    private RequestApprovalUseCase requestApprovalUseCase;

    @Autowired
    private ApproveOrdemUseCase approveOrdemUseCase;

    @Autowired
    private ExecuteOrdemUseCase executeOrdemUseCase;

    @Autowired
    private CancelOrdemUseCase cancelOrdemUseCase;

    @Autowired
    private EntregarOrdemUseCase entregarOrdemUseCase;

    @Autowired
    private FinalizeOrdemUseCase finalizeOrdemUseCase;

    @PostMapping
    public ResponseEntity<CreateOSResponseDTO> createOS(
            @RequestBody @Valid CreateOrderRequestDTO createOrderRequestDTO) throws GatewayException, UseCaseException {

        Ordem os = createOrdemUseCase.execute(createOrderRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(os)).body(CreateOSResponseDTO.fromDomain(os));
    }

    @PostMapping("/{osId}/diagnosticar")
    public ResponseEntity<EvaluateOSResponseDTO> evaluate(
            @PathVariable Long osId) throws GatewayException, ValidationException, UseCaseException {

        Ordem os = evaluateOrdemUseCase.execute(osId);

        return ResponseEntity.ok(EvaluateOSResponseDTO.fromDomain(os));
    }

    @PostMapping("/{osId}/adicionarServicos")
    public ResponseEntity<AddServicosResponseDTO> addServicos(
            @PathVariable Long osId, @RequestBody @Valid AddServicosRequestDTO createOSRequestDTO) throws GatewayException, ValidationException, UseCaseException {

        Ordem os = addServicosOSService.execute(createOSRequestDTO.toDomain(osId));

        return ResponseEntity.ok(AddServicosResponseDTO.fromDomain(os));
    }

    @PostMapping("/{osId}/solicitarAprovacao")
    public ResponseEntity<RequestApprovalResponseDTO> requestApproval(
            @PathVariable Long osId) throws GatewayException, ValidationException, UseCaseException {

        Ordem os = requestApprovalUseCase.execute(osId);

        return ResponseEntity.ok(RequestApprovalResponseDTO.fromDomain(os));
    }

    @PostMapping("/{osId}/aprovar")
    public ResponseEntity<ApproveOSResponseDTO> approve(
            @PathVariable Long osId,
            @RequestBody(required = false) AprovarOSRequestDTO request) throws GatewayException, ValidationException, UseCaseException {

        Ordem os;

        if (request == null || request.getServicosAprovados() == null || request.getServicosAprovados().isEmpty()) {
            os = approveOrdemUseCase.approveAll(osId);
        } else {
            os = approveOrdemUseCase.parcialApprove(osId, request.getServicosAprovados());
        }

        return ResponseEntity.ok(ApproveOSResponseDTO.fromDomain(os));
    }

    @PostMapping("/{osId}/executar")
    public ResponseEntity<ExecuteOSResponseDTO> execute(
            @PathVariable Long osId) throws GatewayException, ValidationException, UseCaseException {

        Ordem os = executeOrdemUseCase.execute(osId);

        return ResponseEntity.ok(ExecuteOSResponseDTO.fromDomain(os));
    }

    @PostMapping("/{osId}/finalizar")
    public ResponseEntity<FinalizeOSResponseDTO> finalizar(
            @PathVariable Long osId) throws GatewayException, ValidationException, UseCaseException {

        Ordem os = finalizeOrdemUseCase.execute(osId);

        return ResponseEntity.ok(FinalizeOSResponseDTO.fromDomain(os));
    }

    @PostMapping("/{osId}/cancelar")
    public ResponseEntity<CancelOSResponseDTO> cancel(
            @PathVariable Long osId) throws GatewayException, ValidationException, UseCaseException {

        Ordem os = cancelOrdemUseCase.execute(osId);

        return ResponseEntity.ok(CancelOSResponseDTO.fromDomain(os));
    }

    @PostMapping("/{osId}/entregar")
    public ResponseEntity<EntregarOSResponseDTO> deliver(
            @PathVariable Long osId) throws GatewayException, ValidationException, UseCaseException {

        Ordem os = entregarOrdemUseCase.execute(osId);

        return ResponseEntity.ok(EntregarOSResponseDTO.fromDomain(os));
    }

    @GetMapping("/{osId}")
    public ResponseEntity<FindOSResponseDTO> findOS(
            @PathVariable Long osId) throws GatewayException {

        Ordem os = findOrdemGateway.execute(osId);
        return ResponseEntity.ok(FindOSResponseDTO.fromDomain(os));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<OSInfoResponseDTO>> listOSs(
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String documentoCliente,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Complexidade complexidade,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @PageableDefault(size = 20, page = 0) Pageable pageable) throws GatewayException {

        Page<Ordem> ordemDeServicos = listOrdemGateway.execute(
                placa, documentoCliente, status, complexidade, dataInicio, dataFim, pageable);

        return ResponseEntity.ok(OSInfoResponseDTO.fromDomain(ordemDeServicos));
    }

    private URI buildLocationUri(Ordem os) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(os.getId())
                .toUri();
    }
}