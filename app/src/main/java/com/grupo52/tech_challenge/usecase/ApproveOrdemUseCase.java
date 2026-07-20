package com.grupo52.tech_challenge.usecase;

import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;

import java.util.List;

public interface ApproveOrdemUseCase {
    Ordem approveAll(Long osId) throws GatewayException, UseCaseException, ValidationException;
    Ordem parcialApprove(Long osId, List<Long> servicosAprovados) throws GatewayException, UseCaseException, ValidationException;
}