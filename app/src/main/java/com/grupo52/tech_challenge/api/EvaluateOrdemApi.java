package com.grupo52.tech_challenge.api;

import com.grupo52.tech_challenge.dto.response.EvaluateOSResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface EvaluateOrdemApi {

    @PostMapping("/ordensDeServico/{osId}/diagnosticar")
    ResponseEntity<EvaluateOSResponseDTO> execute(
            @PathVariable Long osId) throws GatewayException, ValidationException, UseCaseException;
}