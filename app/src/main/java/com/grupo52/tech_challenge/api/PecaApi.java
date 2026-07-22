package com.grupo52.tech_challenge.api;

import com.grupo52.tech_challenge.dto.request.CreatePecaRequestDTO;
import com.grupo52.tech_challenge.dto.request.UpdatePecaRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreatePecaResponseDTO;
import com.grupo52.tech_challenge.dto.response.FindPecaResponseDTO;
import com.grupo52.tech_challenge.dto.response.UpdatePecaResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/produtos/pecas")
public interface PecaApi {

    @PostMapping
    ResponseEntity<CreatePecaResponseDTO> createPeca(
            @RequestBody @Valid CreatePecaRequestDTO createPecaRequestDTO) throws GatewayException;

    @GetMapping("/{pecaId}")
    ResponseEntity<FindPecaResponseDTO> findPeca(
            @PathVariable Long pecaId) throws GatewayException;

    @PutMapping("/{pecaId}")
    ResponseEntity<UpdatePecaResponseDTO> updatePeca(
            @PathVariable Long pecaId,
            @RequestBody @Valid UpdatePecaRequestDTO updatePecaRequestDTO) throws GatewayException;

    @DeleteMapping("/{pecaId}")
    ResponseEntity<Void> deletePeca(
            @PathVariable Long pecaId) throws GatewayException;
}