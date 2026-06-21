package com.grupo52.tech_challenge.service.impl;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusChangeException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.service.UpdateOSStatusService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class UpdateOSStatusServiceImpl implements UpdateOSStatusService {

    private static final Map<StatusOS, Set<StatusOS>> VALID_TRANSITIONS = Map.of(
            StatusOS.RECEBIDA,              Set.of(StatusOS.EM_DIAGNOSTICO, StatusOS.CANCELADA),
            StatusOS.EM_DIAGNOSTICO,        Set.of(StatusOS.AGUARDANDO_APROVACAO, StatusOS.CANCELADA),
            StatusOS.AGUARDANDO_APROVACAO,  Set.of(StatusOS.APROVADA, StatusOS.CANCELADA),
            StatusOS.APROVADA,              Set.of(StatusOS.EM_EXECUCAO, StatusOS.CANCELADA),
            StatusOS.EM_EXECUCAO,           Set.of(StatusOS.FINALIZADA, StatusOS.CANCELADA),
            StatusOS.FINALIZADA,            Set.of(StatusOS.ENTREGUE),
            StatusOS.ENTREGUE,              Set.of(),
            StatusOS.CANCELADA,             Set.of(StatusOS.DEVOLVIDO)
    );

    @Override
    public void execute(OrdemDeServico os, StatusOS newStatus) throws GatewayException, ValidationException {
        try {
            if (!validateStatusChange(os.getStatus(), newStatus)) {
                throw new InvalidStatusChangeException(
                        "Mudança de status não permitida, OS não pode mudar de '" + os.getStatus() + "' para '" + newStatus + "'"
                );
            }
            os.setStatus(newStatus);
        } catch (InvalidStatusChangeException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException(e.getClass().getSimpleName());
        }
    }

    private Boolean validateStatusChange(StatusOS currentStatus, StatusOS newStatus) {
        if (currentStatus == null) {
            return newStatus == StatusOS.RECEBIDA;
        }
        Set<StatusOS> allowed = VALID_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        return allowed.contains(newStatus);
    }
}