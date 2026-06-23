package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;

import java.util.List;

public interface ApproveOSService {
    OrdemDeServico approveAll(Long osId) throws GatewayException, ServiceException, ValidationException;
    OrdemDeServico parcialApprove(Long osId, List<Long> servicosAprovados) throws GatewayException, ServiceException, ValidationException;
}