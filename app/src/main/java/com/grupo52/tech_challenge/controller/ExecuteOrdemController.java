package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.ExecuteOrdemApi;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.dto.response.ExecuteOSResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.usecase.ExecuteOrdemUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class ExecuteOrdemController implements ExecuteOrdemApi {

    @Autowired
    private ExecuteOrdemUseCase executeOrdemUseCase;

    @Override
    public ResponseEntity<ExecuteOSResponseDTO> execute(Long osId) throws GatewayException, ValidationException, UseCaseException {
        Ordem os = executeOrdemUseCase.execute(osId);

        return ResponseEntity.ok(ExecuteOSResponseDTO.fromDomain(os));
    }
}