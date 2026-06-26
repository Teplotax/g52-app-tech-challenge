package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.ClearTagChaveGateway;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.SendNotaFiscalEmailGateway;
import com.grupo52.tech_challenge.gateway.UpdateOSGateway;
import com.grupo52.tech_challenge.service.impl.EntregarOSServiceImpl;
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
class EntregarOSServiceImplTest {

    @Mock
    private FindOSGateway findOSGateway;

    @Mock
    private UpdateOSStatusService updateOSStatusService;

    @Mock
    private UpdateOSGateway updateOSGateway;

    @Mock
    private ClearTagChaveGateway clearTagChaveGateway;

    @Mock
    private SendNotaFiscalEmailGateway sendNotaFiscalEmailGateway;

    @InjectMocks
    private EntregarOSServiceImpl entregarOSService;

    @Test
    public void entregarFromFinalizadaTransitionsToEntregueAndSendsEmail() throws GatewayException, ValidationException, ServiceException {
        Long osId = 1L;
        OrdemDeServico os = OrdemDeServicoFixture.finalizada(osId);
        OrdemDeServico updated = OrdemDeServicoFixture.entregue(osId);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            OrdemDeServico osArg = invocation.getArgument(0);
            osArg.setStatus(invocation.getArgument(1));
            return null;
        }).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenReturn(updated);
        doNothing().when(clearTagChaveGateway).execute(any(Long.class));
        doNothing().when(sendNotaFiscalEmailGateway).execute(any(OrdemDeServico.class));

        OrdemDeServico result = entregarOSService.execute(osId);

        assertEquals(StatusOS.ENTREGUE, result.getStatus());
        verify(updateOSStatusService, times(1)).execute(os, StatusOS.ENTREGUE);
        verify(updateOSGateway, times(1)).execute(os);
        verify(clearTagChaveGateway, times(1)).execute(osId);
        verify(sendNotaFiscalEmailGateway, times(1)).execute(updated);
        verifyNoMoreInteractions(updateOSStatusService, updateOSGateway, clearTagChaveGateway, sendNotaFiscalEmailGateway);
    }

    @Test
    public void entregarFromCanceladaTransitionsToDevolvido() throws GatewayException, ValidationException, ServiceException {
        Long osId = 1L;
        OrdemDeServico os = OrdemDeServicoFixture.cancelada(osId);
        OrdemDeServico updated = OrdemDeServicoFixture.devolvido(osId);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            OrdemDeServico osArg = invocation.getArgument(0);
            osArg.setStatus(invocation.getArgument(1));
            return null;
        }).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenReturn(updated);
        doNothing().when(clearTagChaveGateway).execute(any(Long.class));

        OrdemDeServico result = entregarOSService.execute(osId);

        assertEquals(StatusOS.DEVOLVIDO, result.getStatus());
        verify(updateOSStatusService, times(1)).execute(os, StatusOS.DEVOLVIDO);
        verify(updateOSGateway, times(1)).execute(os);
        verify(clearTagChaveGateway, times(1)).execute(osId);
        verify(sendNotaFiscalEmailGateway, never()).execute(any());
    }

    @Test
    public void onInvalidStatusThrowsInvalidStatusException() throws GatewayException {
        OrdemDeServico os = OrdemDeServicoFixture.aguardandoAprovacao(1L);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);

        assertThrows(InvalidStatusException.class, () -> entregarOSService.execute(1L));

        verifyNoInteractions(updateOSStatusService, updateOSGateway, clearTagChaveGateway, sendNotaFiscalEmailGateway);
    }

    @Test
    public void onGatewayException() throws GatewayException {
        doThrow(GatewayException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> entregarOSService.execute(1L));
    }

    @Test
    public void onValidationException() throws GatewayException, ValidationException, ServiceException {
        OrdemDeServico os = OrdemDeServicoFixture.finalizada(1L);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doThrow(ValidationException.class).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));

        assertThrows(ValidationException.class, () -> entregarOSService.execute(1L));

        verifyNoInteractions(updateOSGateway, clearTagChaveGateway, sendNotaFiscalEmailGateway);
    }

    @Test
    public void onEmailGatewayException() throws GatewayException, ValidationException, ServiceException {
        Long osId = 1L;
        OrdemDeServico os = OrdemDeServicoFixture.finalizada(osId);
        OrdemDeServico updated = OrdemDeServicoFixture.entregue(osId);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doNothing().when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenReturn(updated);
        doNothing().when(clearTagChaveGateway).execute(any(Long.class));
        doThrow(GatewayException.class).when(sendNotaFiscalEmailGateway).execute(any(OrdemDeServico.class));

        assertThrows(GatewayException.class, () -> entregarOSService.execute(osId));
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(ServiceException.class, () -> entregarOSService.execute(1L));
    }
}