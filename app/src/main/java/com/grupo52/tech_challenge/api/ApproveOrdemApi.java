package com.grupo52.tech_challenge.api;

import com.grupo52.tech_challenge.dto.request.AprovarOSRequestDTO;
import com.grupo52.tech_challenge.dto.response.ApproveOSResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ApproveOrdemApi {

    @PostMapping("/ordensDeServico/{osId}/aprovar")
    ResponseEntity<ApproveOSResponseDTO> execute(
            @PathVariable Long osId,
            @RequestBody(required = false) AprovarOSRequestDTO request) throws GatewayException, ValidationException, UseCaseException;
}