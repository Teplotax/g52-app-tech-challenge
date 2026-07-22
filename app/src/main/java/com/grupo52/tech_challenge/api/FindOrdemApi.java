package com.grupo52.tech_challenge.api;

import com.grupo52.tech_challenge.dto.response.FindOSResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface FindOrdemApi {

    @GetMapping("/ordensDeServico/{osId}")
    ResponseEntity<FindOSResponseDTO> execute(
            @PathVariable Long osId) throws GatewayException;
}