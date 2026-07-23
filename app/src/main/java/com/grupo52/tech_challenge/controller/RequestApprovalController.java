package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.RequestApprovalApi;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.dto.response.RequestApprovalResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.usecase.RequestApprovalUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class RequestApprovalController implements RequestApprovalApi {

    @Autowired
    private RequestApprovalUseCase requestApprovalUseCase;

    @Override
    public ResponseEntity<RequestApprovalResponseDTO> execute(Long osId) throws GatewayException, ValidationException, UseCaseException {
        Ordem os = requestApprovalUseCase.execute(osId);

        return ResponseEntity.ok(RequestApprovalResponseDTO.fromDomain(os));
    }
}