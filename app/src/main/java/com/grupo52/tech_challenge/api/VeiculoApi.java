package com.grupo52.tech_challenge.api;

import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.request.CreateVeiculoRequestDTO;
import com.grupo52.tech_challenge.dto.request.UpdateVeiculoRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateVeiculoResponseDTO;
import com.grupo52.tech_challenge.dto.response.FindVeiculoResponseDTO;
import com.grupo52.tech_challenge.dto.response.VeiculoInfoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.validation.annotation.Placa;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/veiculos")
public interface VeiculoApi {

    @PostMapping
    ResponseEntity<CreateVeiculoResponseDTO> createVeiculo(
            @RequestBody @Valid CreateVeiculoRequestDTO createVeiculoRequestDTO) throws GatewayException;

    @GetMapping("/{veiculoId}")
    ResponseEntity<FindVeiculoResponseDTO> findVeiculo(
            @PathVariable Long veiculoId) throws GatewayException;

    @GetMapping("/placa/{placa}")
    ResponseEntity<FindVeiculoResponseDTO> findVeiculo(
            @PathVariable @Placa String placa) throws GatewayException;

    @GetMapping
    ResponseEntity<PagedResponse<VeiculoInfoResponseDTO>> listVeiculos(
            @PageableDefault(size = 20, page = 0) Pageable pageable) throws GatewayException;

    @PutMapping("/{veiculoId}")
    ResponseEntity<VeiculoInfoResponseDTO> updateVeiculo(
            @PathVariable Long veiculoId,
            @RequestBody @Valid UpdateVeiculoRequestDTO updateVeiculoRequestDTO) throws GatewayException;

    @DeleteMapping("/{veiculoId}")
    ResponseEntity<Void> deleteVeiculo(
            @PathVariable Long veiculoId) throws GatewayException;
}