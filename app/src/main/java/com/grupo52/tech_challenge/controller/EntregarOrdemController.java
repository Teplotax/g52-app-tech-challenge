package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.EntregarOrdemApi;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.dto.response.EntregarOSResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.usecase.EntregarOrdemUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class EntregarOrdemController implements EntregarOrdemApi {

    @Autowired
    private EntregarOrdemUseCase entregarOrdemUseCase;

    @Override
    public ResponseEntity<EntregarOSResponseDTO> execute(Long osId) throws GatewayException, ValidationException, UseCaseException {
        Ordem os = entregarOrdemUseCase.execute(osId);

        return ResponseEntity.ok(EntregarOSResponseDTO.fromDomain(os));
    }
}