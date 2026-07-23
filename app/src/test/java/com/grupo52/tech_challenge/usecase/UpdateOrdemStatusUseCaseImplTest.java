package com.grupo52.tech_challenge.usecase;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusChangeException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.usecase.impl.UpdateOrdemStatusUseCaseImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateOrdemStatusUseCaseImplTest {

    private final UpdateOrdemStatusUseCaseImpl updateOSStatusService = new UpdateOrdemStatusUseCaseImpl();

    @Test
    public void executeSuccess() throws GatewayException, ValidationException, UseCaseException {
        Ordem os = OrdemDeServicoFixture.recebida(1L);

        updateOSStatusService.execute(os, Status.EM_DIAGNOSTICO);

        assertEquals(Status.EM_DIAGNOSTICO, os.getStatus());
        assertTrue(os.getHistorico().stream().anyMatch(statusChange -> statusChange.getStatus() == Status.EM_DIAGNOSTICO));
    }

    @Test
    public void executeAguardandoAprovacaoParaAguardandoAquisicao() throws GatewayException, ValidationException, UseCaseException {
        Ordem os = OrdemDeServicoFixture.aguardandoAprovacao(1L);

        updateOSStatusService.execute(os, Status.AGUARDANDO_AQUISICAO);

        assertEquals(Status.AGUARDANDO_AQUISICAO, os.getStatus());
    }

    @Test
    public void executeAguardandoAquisicaoParaAprovada() throws GatewayException, ValidationException, UseCaseException {
        Ordem os = Ordem.builder().id(1L).status(Status.AGUARDANDO_AQUISICAO).build();

        updateOSStatusService.execute(os, Status.APROVADA);

        assertEquals(Status.APROVADA, os.getStatus());
    }

    @Test
    public void executeAguardandoAquisicaoParaCancelada() throws GatewayException, ValidationException, UseCaseException {
        Ordem os = Ordem.builder().id(1L).status(Status.AGUARDANDO_AQUISICAO).build();

        updateOSStatusService.execute(os, Status.CANCELADA);

        assertEquals(Status.CANCELADA, os.getStatus());
    }

    @Test
    public void onInvalidTransitionFromAguardandoAquisicao() {
        Ordem os = Ordem.builder().id(1L).status(Status.AGUARDANDO_AQUISICAO).build();

        assertThrows(InvalidStatusChangeException.class, () -> {
            updateOSStatusService.execute(os, Status.EM_EXECUCAO);
        });
    }

    @Test
    public void onNewOSWithoutCurrentStatus() throws GatewayException, ValidationException, UseCaseException {
        Ordem os = Ordem.builder().id(1L).build();

        updateOSStatusService.execute(os, Status.RECEBIDA);

        assertEquals(Status.RECEBIDA, os.getStatus());
    }

    @Test
    public void onNewOSWithInvalidInitialStatus() {
        Ordem os = Ordem.builder().id(1L).build();

        assertThrows(InvalidStatusChangeException.class, () -> {
            updateOSStatusService.execute(os, Status.EM_DIAGNOSTICO);
        });
    }

    @Test
    public void onInvalidTransition() {
        Ordem os = OrdemDeServicoFixture.emDiagnostico(1L);

        assertThrows(InvalidStatusChangeException.class, () -> {
            updateOSStatusService.execute(os, Status.RECEBIDA);
        });
    }

    @Test
    public void onTerminalStatus() {
        Ordem os = Ordem.builder().id(1L).status(Status.ENTREGUE).build();

        assertThrows(InvalidStatusChangeException.class, () -> {
            updateOSStatusService.execute(os, Status.RECEBIDA);
        });
    }

    @Test
    public void onUnexpectedException() {
        assertThrows(UseCaseException.class, () -> {
            updateOSStatusService.execute(null, Status.RECEBIDA);
        });
    }
}