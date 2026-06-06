package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.request.CreateVeiculoRequestDTO;
import com.grupo52.tech_challenge.dto.request.UpdateVeiculoRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateVeiculoResponseDTO;
import com.grupo52.tech_challenge.dto.response.FindVeiculoResponseDTO;
import com.grupo52.tech_challenge.dto.response.VeiculoInfoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private CreateVeiculoGateway createVeiculoGateway;

    @Autowired
    private FindVeiculoGateway findVeiculoGateway;

    @Autowired
    private FindVeiculoByPlacaGateway findVeiculoByPlacaGateway;

    @Autowired
    private ListVeiculosGateway listVeiculosGateway;

    @Autowired
    private UpdateVeiculoGateway updateVeiculoGateway;

    @Autowired
    private DeleteVeiculoGateway deleteVeiculoGateway;

    @PostMapping
    public ResponseEntity<CreateVeiculoResponseDTO> createCliente(@RequestBody @Valid CreateVeiculoRequestDTO createVeiculoRequestDTO) throws GatewayException {
        Veiculo veiculo = createVeiculoGateway.execute(createVeiculoRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(veiculo)).body(CreateVeiculoResponseDTO.fromDomain(veiculo));
    }

    @GetMapping("/{veiculoId}")
    public ResponseEntity<FindVeiculoResponseDTO> findVeiculo(@PathVariable Long veiculoId) throws GatewayException {
        Veiculo veiculo = findVeiculoGateway.execute(veiculoId);

        return ResponseEntity.ok().body(FindVeiculoResponseDTO.fromDomain(veiculo));
    }

    @GetMapping("/placa/{placa}")
    public ResponseEntity<FindVeiculoResponseDTO> findVeiculo(@PathVariable String placa) throws GatewayException {
        Veiculo veiculo = findVeiculoByPlacaGateway.execute(placa);

        return ResponseEntity.ok().body(FindVeiculoResponseDTO.fromDomain(veiculo));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<VeiculoInfoResponseDTO>> listVeiculos(
            @PageableDefault(size = 20, page = 0) Pageable pageable) throws GatewayException {
        Page<Veiculo> veiculos = listVeiculosGateway.execute(pageable);

        return ResponseEntity.ok(VeiculoInfoResponseDTO.fromDomain(veiculos));
    }

    @PutMapping("/{veiculoId}")
    public ResponseEntity<VeiculoInfoResponseDTO> updateVeiculo(
            @PathVariable Long veiculoId,
            @RequestBody @Valid UpdateVeiculoRequestDTO updateVeiculoRequestDTO) throws GatewayException {
        Veiculo veiculo = updateVeiculoGateway.execute(updateVeiculoRequestDTO.toDomain(veiculoId));

        return ResponseEntity.ok().body(VeiculoInfoResponseDTO.fromDomain(veiculo));
    }

    @DeleteMapping("/{veiculoId}")
    public ResponseEntity<Void> deleteVeiculo(@PathVariable Long veiculoId) throws GatewayException {
        deleteVeiculoGateway.execute(veiculoId);

        return ResponseEntity.noContent().header("Location", buildLocationUri()).build();
    }

    private URI buildLocationUri(Veiculo veiculo) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(veiculo.getId())
                .toUri();
    }

    private String buildLocationUri() {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build().toString();
    }
}
