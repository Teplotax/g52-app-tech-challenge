package com.grupo52.tech_challenge.api;

import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.request.CreateServicoRequestDTO;
import com.grupo52.tech_challenge.dto.request.UpdateServicoRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateServicoResponseDTO;
import com.grupo52.tech_challenge.dto.response.FindServicoResponseDTO;
import com.grupo52.tech_challenge.dto.response.ServicoInfoResponseDTO;
import com.grupo52.tech_challenge.dto.response.UpdateServicoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/servicos")
public interface ServicoApi {

    @PostMapping
    ResponseEntity<CreateServicoResponseDTO> createServico(
            @RequestBody @Valid CreateServicoRequestDTO createServicoRequestDTO) throws GatewayException;

    @GetMapping
    ResponseEntity<PagedResponse<ServicoInfoResponseDTO>> listServicos(
            @PageableDefault(size = 20, page = 0) Pageable pageable) throws GatewayException;

    @GetMapping("/{servicoId}")
    ResponseEntity<FindServicoResponseDTO> findServico(
            @PathVariable Long servicoId) throws GatewayException;

    @PutMapping("/{servicoId}")
    ResponseEntity<UpdateServicoResponseDTO> updateServico(
            @PathVariable Long servicoId,
            @RequestBody @Valid UpdateServicoRequestDTO updateServicoRequestDTO) throws GatewayException;

    @DeleteMapping("/{servicoId}")
    ResponseEntity<Void> deleteServico(
            @PathVariable Long servicoId) throws GatewayException;
}