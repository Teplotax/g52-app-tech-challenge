package com.grupo52.tech_challenge.usecase;

import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;

public interface AddServicosUseCase {
    Ordem execute(Ordem os) throws GatewayException, UseCaseException, ValidationException;
}
