package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.FinalizeOrdemApi;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.dto.response.FinalizeOSResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.usecase.FinalizeOrdemUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class FinalizeOrdemController implements FinalizeOrdemApi {

    @Autowired
    private FinalizeOrdemUseCase finalizeOrdemUseCase;

    @Override
    public ResponseEntity<FinalizeOSResponseDTO> execute(Long osId) throws GatewayException, ValidationException, UseCaseException {
        Ordem os = finalizeOrdemUseCase.execute(osId);

        return ResponseEntity.ok(FinalizeOSResponseDTO.fromDomain(os));
    }
}