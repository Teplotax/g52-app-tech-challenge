package com.grupo52.tech_challenge.usecase.impl;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusChangeException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.usecase.UpdateOrdemStatusUseCase;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class UpdateOrdemStatusUseCaseImpl implements UpdateOrdemStatusUseCase {

    private static final Map<Status, Set<Status>> VALID_TRANSITIONS = Map.of(
            Status.RECEBIDA,              Set.of(Status.EM_DIAGNOSTICO, Status.CANCELADA),
            Status.EM_DIAGNOSTICO,        Set.of(Status.AGUARDANDO_APROVACAO, Status.CANCELADA),
            Status.AGUARDANDO_APROVACAO,  Set.of(Status.APROVADA, Status.AGUARDANDO_AQUISICAO, Status.CANCELADA),
            Status.AGUARDANDO_AQUISICAO,  Set.of(Status.APROVADA, Status.CANCELADA),
            Status.APROVADA,              Set.of(Status.EM_EXECUCAO, Status.CANCELADA),
            Status.EM_EXECUCAO,           Set.of(Status.FINALIZADA, Status.CANCELADA),
            Status.FINALIZADA,            Set.of(Status.ENTREGUE),
            Status.ENTREGUE,              Set.of(),
            Status.CANCELADA,             Set.of(Status.DEVOLVIDO)
    );

    @Override
    public void execute(Ordem os, Status newStatus) throws GatewayException, ValidationException, UseCaseException {
        try {
            if (!validateStatusChange(os.getStatus(), newStatus)) {
                throw new InvalidStatusChangeException(
                        "Mudança de status não permitida, a ordem de serviço não pode mudar de '" + os.getStatus() + "' para '" + newStatus + "'"
                );
            }
            os.setStatus(newStatus);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new UseCaseException("Falha inesperada ao atualizar status da OS", e);
        }
    }

    private Boolean validateStatusChange(Status currentStatus, Status newStatus) {
        if (currentStatus == null) {
            return newStatus == Status.RECEBIDA;
        }
        Set<Status> allowed = VALID_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        return allowed.contains(newStatus);
    }
}