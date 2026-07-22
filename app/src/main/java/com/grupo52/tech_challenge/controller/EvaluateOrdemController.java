package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.EvaluateOrdemApi;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.dto.response.EvaluateOSResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.usecase.EvaluateOrdemUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class EvaluateOrdemController implements EvaluateOrdemApi {

    @Autowired
    private EvaluateOrdemUseCase evaluateOrdemUseCase;

    @Override
    public ResponseEntity<EvaluateOSResponseDTO> execute(Long osId) throws GatewayException, ValidationException, UseCaseException {
        Ordem os = evaluateOrdemUseCase.execute(osId);

        return ResponseEntity.ok(EvaluateOSResponseDTO.fromDomain(os));
    }
}