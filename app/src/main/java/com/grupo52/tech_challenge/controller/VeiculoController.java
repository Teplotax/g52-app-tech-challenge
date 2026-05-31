package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.dto.request.CreateVeiculoRequestDTO;
import com.grupo52.tech_challenge.dto.request.UpdateVeiculoRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateVeiculoResponseDTO;
import com.grupo52.tech_challenge.dto.response.VeiculoInfoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreateVeiculoGateway;
import com.grupo52.tech_challenge.gateway.DeleteVeiculoGateway;
import com.grupo52.tech_challenge.gateway.FindVeiculoGateway;
import com.grupo52.tech_challenge.gateway.UpdateVeiculoGateway;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UpdateVeiculoGateway updateVeiculoGateway;

    @Autowired
    private DeleteVeiculoGateway deleteVeiculoGateway;

    @PostMapping
    public ResponseEntity<CreateVeiculoResponseDTO> createCliente(@RequestBody @Valid CreateVeiculoRequestDTO createVeiculoRequestDTO) throws GatewayException {
        Veiculo veiculo = createVeiculoGateway.execute(createVeiculoRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(veiculo)).body(CreateVeiculoResponseDTO.fromDomain(veiculo));
    }

    @GetMapping("/{veiculoId}")
    public ResponseEntity<VeiculoInfoResponseDTO> findVeiculo(@PathVariable Long veiculoId) throws GatewayException {
        Veiculo veiculo = findVeiculoGateway.execute(veiculoId);

        return ResponseEntity.ok().body(VeiculoInfoResponseDTO.fromDomain(veiculo));
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

        return ResponseEntity.noContent().build();
    }

    private URI buildLocationUri(Veiculo veiculo) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(veiculo.getId())
                .toUri();
    }
}
