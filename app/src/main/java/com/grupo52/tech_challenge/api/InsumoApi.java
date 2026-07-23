package com.grupo52.tech_challenge.api;

import com.grupo52.tech_challenge.dto.request.CreateInsumoRequestDTO;
import com.grupo52.tech_challenge.dto.request.UpdateInsumoRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateInsumoResponseDTO;
import com.grupo52.tech_challenge.dto.response.FindInsumoResponseDTO;
import com.grupo52.tech_challenge.dto.response.UpdateInsumoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/produtos/insumos")
public interface InsumoApi {

    @PostMapping
    ResponseEntity<CreateInsumoResponseDTO> createInsumo(
            @RequestBody @Valid CreateInsumoRequestDTO createInsumoRequestDTO) throws GatewayException;

    @GetMapping("/{insumoId}")
    ResponseEntity<FindInsumoResponseDTO> findInsumo(
            @PathVariable Long insumoId) throws GatewayException;

    @PutMapping("/{insumoId}")
    ResponseEntity<UpdateInsumoResponseDTO> updateInsumo(
            @PathVariable Long insumoId,
            @RequestBody @Valid UpdateInsumoRequestDTO updateInsumoRequestDTO) throws GatewayException;

    @DeleteMapping("/{insumoId}")
    ResponseEntity<Void> deleteInsumo(
            @PathVariable Long insumoId) throws GatewayException;
}