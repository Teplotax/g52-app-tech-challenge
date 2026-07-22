package com.grupo52.tech_challenge.usecase;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.ClearTagChaveGateway;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.gateway.SendNotaFiscalEmailGateway;
import com.grupo52.tech_challenge.gateway.UpdateOrdemGateway;
import com.grupo52.tech_challenge.usecase.impl.EntregarOrdemUseCaseImpl;
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
class EntregarOrdemUseCaseImplTest {

    @Mock
    private FindOrdemGateway findOrdemGateway;

    @Mock
    private UpdateOrdemStatusUseCase updateOrdemStatusUseCase;

    @Mock
    private UpdateOrdemGateway updateOrdemGateway;

    @Mock
    private ClearTagChaveGateway clearTagChaveGateway;

    @Mock
    private SendNotaFiscalEmailGateway sendNotaFiscalEmailGateway;

    @InjectMocks
    private EntregarOrdemUseCaseImpl entregarOSService;

    @Test
    public void entregarFromFinalizadaTransitionsToEntregueAndSendsEmail() throws GatewayException, ValidationException, UseCaseException {
        Long osId = 1L;
        Ordem os = OrdemDeServicoFixture.finalizada(osId);
        Ordem updated = OrdemDeServicoFixture.entregue(osId);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            Ordem osArg = invocation.getArgument(0);
            osArg.setStatus(invocation.getArgument(1));
            return null;
        }).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenReturn(updated);
        doNothing().when(clearTagChaveGateway).execute(any(Long.class));
        doNothing().when(sendNotaFiscalEmailGateway).execute(any(Ordem.class));

        Ordem result = entregarOSService.execute(osId);

        assertEquals(Status.ENTREGUE, result.getStatus());
        verify(updateOrdemStatusUseCase, times(1)).execute(os, Status.ENTREGUE);
        verify(updateOrdemGateway, times(1)).execute(os);
        verify(clearTagChaveGateway, times(1)).execute(osId);
        verify(sendNotaFiscalEmailGateway, times(1)).execute(updated);
        verifyNoMoreInteractions(updateOrdemStatusUseCase, updateOrdemGateway, clearTagChaveGateway, sendNotaFiscalEmailGateway);
    }

    @Test
    public void entregarFromCanceladaTransitionsToDevolvido() throws GatewayException, ValidationException, UseCaseException {
        Long osId = 1L;
        Ordem os = OrdemDeServicoFixture.cancelada(osId);
        Ordem updated = OrdemDeServicoFixture.devolvido(osId);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            Ordem osArg = invocation.getArgument(0);
            osArg.setStatus(invocation.getArgument(1));
            return null;
        }).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenReturn(updated);
        doNothing().when(clearTagChaveGateway).execute(any(Long.class));

        Ordem result = entregarOSService.execute(osId);

        assertEquals(Status.DEVOLVIDO, result.getStatus());
        verify(updateOrdemStatusUseCase, times(1)).execute(os, Status.DEVOLVIDO);
        verify(updateOrdemGateway, times(1)).execute(os);
        verify(clearTagChaveGateway, times(1)).execute(osId);
        verify(sendNotaFiscalEmailGateway, never()).execute(any());
    }

    @Test
    public void onInvalidStatusThrowsInvalidStatusException() throws GatewayException {
        Ordem os = OrdemDeServicoFixture.aguardandoAprovacao(1L);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);

        assertThrows(InvalidStatusException.class, () -> entregarOSService.execute(1L));

        verifyNoInteractions(updateOrdemStatusUseCase, updateOrdemGateway, clearTagChaveGateway, sendNotaFiscalEmailGateway);
    }

    @Test
    public void onGatewayException() throws GatewayException {
        doThrow(GatewayException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> entregarOSService.execute(1L));
    }

    @Test
    public void onValidationException() throws GatewayException, ValidationException, UseCaseException {
        Ordem os = OrdemDeServicoFixture.finalizada(1L);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doThrow(ValidationException.class).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));

        assertThrows(ValidationException.class, () -> entregarOSService.execute(1L));

        verifyNoInteractions(updateOrdemGateway, clearTagChaveGateway, sendNotaFiscalEmailGateway);
    }

    @Test
    public void onEmailGatewayException() throws GatewayException, ValidationException, UseCaseException {
        Long osId = 1L;
        Ordem os = OrdemDeServicoFixture.finalizada(osId);
        Ordem updated = OrdemDeServicoFixture.entregue(osId);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doNothing().when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenReturn(updated);
        doNothing().when(clearTagChaveGateway).execute(any(Long.class));
        doThrow(GatewayException.class).when(sendNotaFiscalEmailGateway).execute(any(Ordem.class));

        assertThrows(GatewayException.class, () -> entregarOSService.execute(osId));
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(UseCaseException.class, () -> entregarOSService.execute(1L));
    }
}