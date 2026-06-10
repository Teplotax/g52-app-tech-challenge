package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.request.CreateServicoRequestDTO;
import com.grupo52.tech_challenge.dto.request.UpdateServicoRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateServicoResponseDTO;
import com.grupo52.tech_challenge.dto.response.FindServicoResponseDTO;
import com.grupo52.tech_challenge.dto.response.ServicoInfoResponseDTO;
import com.grupo52.tech_challenge.dto.response.UpdateServicoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreateServicoGateway;
import com.grupo52.tech_challenge.gateway.FindServicoGateway;
import com.grupo52.tech_challenge.gateway.ListServicosGateway;
import com.grupo52.tech_challenge.gateway.UpdateServicoGateway;
import com.grupo52.tech_challenge.gateway.database.repository.ServicoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/servicos")
@Validated
@RequiredArgsConstructor
public class ServicoController {

    private final CreateServicoGateway createServicoGateway;
    private final ListServicosGateway listServicosGateway;
    private final FindServicoGateway findServicoGateway;
    private final UpdateServicoGateway updateServicoGateway;
    private final ServicoRepository servicoRepository;

    @PostMapping
    public ResponseEntity<CreateServicoResponseDTO> createServico(
            @RequestBody @Valid CreateServicoRequestDTO createServicoRequestDTO) throws GatewayException {
        Servico servico = createServicoGateway.execute(createServicoRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(servico)).body(CreateServicoResponseDTO.fromDomain(servico));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ServicoInfoResponseDTO>> listServicos(
            @PageableDefault(size = 20, page = 0) Pageable pageable) throws GatewayException {
        Page<Servico> servicos = listServicosGateway.execute(pageable);

        return ResponseEntity.ok(ServicoInfoResponseDTO.fromDomain(servicos));
    }

    @GetMapping("/{servicoId}")
    public ResponseEntity<FindServicoResponseDTO> findServico(@PathVariable Long servicoId) throws GatewayException {
        Servico servico = findServicoGateway.execute(servicoId);

        return ResponseEntity.ok().body(FindServicoResponseDTO.fromDomain(servico));
    }

    @PutMapping("/{servicoId}")
    public ResponseEntity<UpdateServicoResponseDTO> updateServico(
            @PathVariable Long servicoId,
            @RequestBody @Valid UpdateServicoRequestDTO updateServicoRequestDTO) throws GatewayException {
        Servico servico = updateServicoGateway.execute(updateServicoRequestDTO.toDomain(servicoId));

        return ResponseEntity.ok().body(UpdateServicoResponseDTO.fromDomain(servico));
    }

    @DeleteMapping("/{servicoId}")
    public ResponseEntity<Void> deleteServico(@PathVariable Long servicoId) throws GatewayException {
        if (!servicoRepository.existsById(servicoId)) {
            throw new GatewayException("Serviço não encontrado", 404);
        }
        servicoRepository.deleteById(servicoId);

        return ResponseEntity.noContent().header("Location", buildLocationUri()).build();
    }

    private URI buildLocationUri(Servico servico) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(servico.getId())
                .toUri();
    }

    private String buildLocationUri() {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build().toString();
    }
}