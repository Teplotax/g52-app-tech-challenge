package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.dto.request.CreateOSRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateOSResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreateOSGateway;
import com.grupo52.tech_challenge.service.CalculateOSPriceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/ordemsDeServico")
@Validated
public class OrdemDeServicoController {

    @Autowired
    private CreateOSGateway createOSGateway;

    @Autowired
    private CalculateOSPriceService calculateOSPriceService;
//
//    @Autowired
//    private FindOSGateway findOSGateway;
//
//    @Autowired
//    private FindOSByDocumentGateway findOSByDocumentGateway;
//
//    @Autowired
//    private ListOSsGateway listOSsGateway;
//;

    @PostMapping
    public ResponseEntity<CreateOSResponseDTO> createOS(@RequestBody @Valid CreateOSRequestDTO createOSRequestDTO) throws GatewayException {

        OrdemDeServico os = calculateOSPriceService.calculateServicosDesejados(createOSGateway.execute(createOSRequestDTO.toDomain()));

        return ResponseEntity.created(buildLocationUri(os)).body(CreateOSResponseDTO.fromDomain(os));
    }

//    @GetMapping("/{ordemDeServicoId}")
//    public ResponseEntity<FindOSResponseDTO> findOS(@PathVariable Long ordemDeServicoId) throws GatewayException {
//        OS ordemDeServico = findOSGateway.execute(ordemDeServicoId);
//
//        return ResponseEntity.ok().body(FindOSResponseDTO.fromDomain(ordemDeServico));
//    }
//
//    @GetMapping("/documento/{documento}")
//    public ResponseEntity<FindOSResponseDTO> findOSByDocumento(@PathVariable @Documento String documento) throws GatewayException {
//        OS ordemDeServico = findOSByDocumentGateway.execute(documento);
//
//        return ResponseEntity.ok().body(FindOSResponseDTO.fromDomain(ordemDeServico));
//    }
//
//    @GetMapping
//    public ResponseEntity<PagedResponse<OSInfoResponseDTO>> listOSs(
//            @PageableDefault(size = 20, page = 0) Pageable pageable) throws GatewayException {
//        Page<OS> ordemDeServicos = listOSsGateway.execute(pageable);
//
//        return ResponseEntity.ok(OSInfoResponseDTO.fromDomain(ordemDeServicos));
//    }

    private URI buildLocationUri(OrdemDeServico os) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(os.getId())
                .toUri();
    }

    private String buildLocationUri() {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build().toString();
    }
}
