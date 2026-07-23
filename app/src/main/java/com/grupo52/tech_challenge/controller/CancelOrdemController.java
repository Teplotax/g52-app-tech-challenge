package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.CancelOrdemApi;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.dto.response.CancelOSResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.usecase.CancelOrdemUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class CancelOrdemController implements CancelOrdemApi {

    @Autowired
    private CancelOrdemUseCase cancelOrdemUseCase;

    @Override
    public ResponseEntity<CancelOSResponseDTO> execute(Long osId) throws GatewayException, ValidationException, UseCaseException {
        Ordem os = cancelOrdemUseCase.execute(osId);

        return ResponseEntity.ok(CancelOSResponseDTO.fromDomain(os));
    }
}