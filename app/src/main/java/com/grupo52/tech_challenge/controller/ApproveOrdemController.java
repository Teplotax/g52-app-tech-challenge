package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.ApproveOrdemApi;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.dto.request.AprovarOSRequestDTO;
import com.grupo52.tech_challenge.dto.response.ApproveOSResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.usecase.ApproveOrdemUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class ApproveOrdemController implements ApproveOrdemApi {

    @Autowired
    private ApproveOrdemUseCase approveOrdemUseCase;

    @Override
    public ResponseEntity<ApproveOSResponseDTO> execute(Long osId, AprovarOSRequestDTO request) throws GatewayException, ValidationException, UseCaseException {
        Ordem os;

        if (request == null || request.getServicosAprovados() == null || request.getServicosAprovados().isEmpty()) {
            os = approveOrdemUseCase.approveAll(osId);
        } else {
            os = approveOrdemUseCase.parcialApprove(osId, request.getServicosAprovados());
        }

        return ResponseEntity.ok(ApproveOSResponseDTO.fromDomain(os));
    }
}