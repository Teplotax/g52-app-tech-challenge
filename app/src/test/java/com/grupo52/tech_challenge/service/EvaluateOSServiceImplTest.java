package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.UpdateOSGateway;
import com.grupo52.tech_challenge.service.impl.EvaluateOSServiceImpl;
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
class EvaluateOSServiceImplTest {

    @Mock
    private FindOSGateway findOSGateway;

    @Mock
    private UpdateOSGateway updateOSGateway;

    @Mock
    private UpdateOSStatusService updateOSStatusService;

    @InjectMocks
    private EvaluateOSServiceImpl evaluateOSService;

    @Test
    public void executeSuccess() throws GatewayException, ValidationException, ServiceException {
        Long osId = 1L;

        when(findOSGateway.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.recebida(osId));

        doAnswer(invocation -> {
            OrdemDeServico osArg = invocation.getArgument(0);
            StatusOS status = invocation.getArgument(1);
            osArg.setStatus(status);
            return null;
        }).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));

        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenReturn(OrdemDeServicoFixture.emDiagnostico(osId));

        OrdemDeServico result = evaluateOSService.execute(osId);

        verify(findOSGateway, times(1)).execute(any(Long.class));
        verify(updateOSStatusService, times(1)).execute(any(OrdemDeServico.class), any(StatusOS.class));
        verify(updateOSGateway, times(1)).execute(any(OrdemDeServico.class));
        verifyNoMoreInteractions(findOSGateway);
        verifyNoMoreInteractions(updateOSStatusService);
        verifyNoMoreInteractions(updateOSGateway);

        assertEquals(StatusOS.EM_DIAGNOSTICO, result.getStatus());
    }

    @Test
    public void onGatewayException() throws GatewayException {
        Long osId = 1L;

        doThrow(GatewayException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> {
            evaluateOSService.execute(osId);
        });
    }

    @Test
    public void onValidationException() throws GatewayException, ValidationException, ServiceException {
        Long osId = 1L;

        when(findOSGateway.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.recebida(osId));
        doThrow(ValidationException.class).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));

        assertThrows(ValidationException.class, () -> {
            evaluateOSService.execute(osId);
        });
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        Long osId = 1L;

        doThrow(RuntimeException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(ServiceException.class, () -> {
            evaluateOSService.execute(osId);
        });
    }
}