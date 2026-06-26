package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusChangeException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.service.impl.UpdateOSStatusServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateOSStatusServiceImplTest {

    private final UpdateOSStatusServiceImpl updateOSStatusService = new UpdateOSStatusServiceImpl();

    @Test
    public void executeSuccess() throws GatewayException, ValidationException, ServiceException {
        OrdemDeServico os = OrdemDeServicoFixture.recebida(1L);

        updateOSStatusService.execute(os, StatusOS.EM_DIAGNOSTICO);

        assertEquals(StatusOS.EM_DIAGNOSTICO, os.getStatus());
        assertTrue(os.getHistorico().stream().anyMatch(statusChange -> statusChange.getStatus() == StatusOS.EM_DIAGNOSTICO));
    }

    @Test
    public void onNewOSWithoutCurrentStatus() throws GatewayException, ValidationException, ServiceException {
        OrdemDeServico os = OrdemDeServico.builder().id(1L).build();

        updateOSStatusService.execute(os, StatusOS.RECEBIDA);

        assertEquals(StatusOS.RECEBIDA, os.getStatus());
    }

    @Test
    public void onNewOSWithInvalidInitialStatus() {
        OrdemDeServico os = OrdemDeServico.builder().id(1L).build();

        assertThrows(InvalidStatusChangeException.class, () -> {
            updateOSStatusService.execute(os, StatusOS.EM_DIAGNOSTICO);
        });
    }

    @Test
    public void onInvalidTransition() {
        OrdemDeServico os = OrdemDeServicoFixture.emDiagnostico(1L);

        assertThrows(InvalidStatusChangeException.class, () -> {
            updateOSStatusService.execute(os, StatusOS.RECEBIDA);
        });
    }

    @Test
    public void onTerminalStatus() {
        OrdemDeServico os = OrdemDeServico.builder().id(1L).status(StatusOS.ENTREGUE).build();

        assertThrows(InvalidStatusChangeException.class, () -> {
            updateOSStatusService.execute(os, StatusOS.RECEBIDA);
        });
    }

    @Test
    public void onUnexpectedException() {
        assertThrows(ServiceException.class, () -> {
            updateOSStatusService.execute(null, StatusOS.RECEBIDA);
        });
    }
}