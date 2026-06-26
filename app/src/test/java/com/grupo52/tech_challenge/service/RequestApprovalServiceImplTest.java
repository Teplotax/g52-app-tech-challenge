package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.SendOrcamentoEmailGateway;
import com.grupo52.tech_challenge.gateway.UpdateOSGateway;
import com.grupo52.tech_challenge.service.impl.RequestApprovalServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestApprovalServiceImplTest {

    @Mock
    private FindOSGateway findOSGateway;

    @Mock
    private UpdateOSGateway updateOSGateway;

    @Mock
    private UpdateOSStatusService updateOSStatusService;

    @Mock
    private SendOrcamentoEmailGateway sendOrcamentoEmailGateway;

    @InjectMocks
    private RequestApprovalServiceImpl requestApprovalService;

    @Test
    public void executeSuccess() throws GatewayException, ValidationException, ServiceException {
        Long osId = 1L;
        OrdemDeServico os = OrdemDeServicoFixture.emDiagnostico(osId);
        OrdemDeServico updated = OrdemDeServicoFixture.aguardandoAprovacao(osId);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            OrdemDeServico osArg = invocation.getArgument(0);
            osArg.setStatus(invocation.getArgument(1));
            return null;
        }).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenReturn(updated);

        OrdemDeServico result = requestApprovalService.execute(osId);

        assertEquals(StatusOS.AGUARDANDO_APROVACAO, result.getStatus());

        verify(findOSGateway, times(1)).execute(osId);
        verify(updateOSStatusService, times(1)).execute(os, StatusOS.AGUARDANDO_APROVACAO);
        verify(updateOSGateway, times(1)).execute(os);
        verify(sendOrcamentoEmailGateway, times(1)).execute(updated);
        verifyNoMoreInteractions(findOSGateway, updateOSStatusService, updateOSGateway, sendOrcamentoEmailGateway);
    }

    @Test
    public void onGatewayExceptionFromFind() throws GatewayException {
        doThrow(GatewayException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> requestApprovalService.execute(1L));
    }

    @Test
    public void onValidationException() throws GatewayException, ValidationException, ServiceException {
        OrdemDeServico os = OrdemDeServicoFixture.emDiagnostico(1L);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doThrow(ValidationException.class).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));

        assertThrows(ValidationException.class, () -> requestApprovalService.execute(1L));

        verify(sendOrcamentoEmailGateway, never()).execute(any());
    }

    @Test
    public void onGatewayExceptionFromEmail() throws GatewayException, ValidationException, ServiceException {
        Long osId = 1L;
        OrdemDeServico os = OrdemDeServicoFixture.emDiagnostico(osId);
        OrdemDeServico updated = OrdemDeServicoFixture.aguardandoAprovacao(osId);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doNothing().when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenReturn(updated);
        doThrow(GatewayException.class).when(sendOrcamentoEmailGateway).execute(any(OrdemDeServico.class));

        assertThrows(GatewayException.class, () -> requestApprovalService.execute(osId));
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(ServiceException.class, () -> requestApprovalService.execute(1L));
    }
}