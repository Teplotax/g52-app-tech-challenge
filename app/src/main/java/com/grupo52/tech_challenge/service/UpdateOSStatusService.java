package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusChangeException;

public interface UpdateOSStatusService {
    void execute(OrdemDeServico os, StatusOS status) throws GatewayException, InvalidStatusChangeException;
}
