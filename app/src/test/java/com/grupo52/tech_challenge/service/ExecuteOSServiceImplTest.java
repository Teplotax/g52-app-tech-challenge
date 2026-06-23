package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.UpdateOSGateway;
import com.grupo52.tech_challenge.service.impl.ExecuteOSServiceImpl;
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
public class ExecuteOSServiceImplTest {

    @Mock
    private FindOSGateway findOSGateway;

    @Mock
    private UpdateOSGateway updateOSGateway;

    @Mock
    private UpdateOSStatusService updateOSStatusService;

    @InjectMocks
    private ExecuteOSServiceImpl executeOSService;

    @Test
    public void executeSuccess() throws GatewayException, ValidationException, ServiceException {
        Long osId = 1L;
        OrdemDeServico os = OrdemDeServicoFixture.aguardandoAprovacao(osId);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            OrdemDeServico osArg = invocation.getArgument(0);
            osArg.setStatus(invocation.getArgument(1));
            return null;
        }).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenReturn(os);

        OrdemDeServico result = executeOSService.execute(osId);

        assertEquals(StatusOS.EM_EXECUCAO, result.getStatus());

        verify(findOSGateway, times(1)).execute(osId);
        verify(updateOSStatusService, times(1)).execute(os, StatusOS.EM_EXECUCAO);
        verify(updateOSGateway, times(1)).execute(os);
        verifyNoMoreInteractions(findOSGateway, updateOSStatusService, updateOSGateway);
    }

    @Test
    public void onGatewayException() throws GatewayException {
        doThrow(GatewayException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> executeOSService.execute(1L));
    }

    @Test
    public void onValidationException() throws GatewayException, ValidationException, ServiceException {
        OrdemDeServico os = OrdemDeServicoFixture.aguardandoAprovacao(1L);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doThrow(ValidationException.class).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));

        assertThrows(ValidationException.class, () -> executeOSService.execute(1L));

        verify(updateOSGateway, never()).execute(any());
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(ServiceException.class, () -> executeOSService.execute(1L));
    }
}