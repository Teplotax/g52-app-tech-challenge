package com.grupo52.tech_challenge.usecase;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.gateway.UpdateOrdemGateway;
import com.grupo52.tech_challenge.usecase.impl.EvaluateOrdemUseCaseImpl;
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
class EvaluateOrdemUseCaseImplTest {

    @Mock
    private FindOrdemGateway findOrdemGateway;

    @Mock
    private UpdateOrdemGateway updateOrdemGateway;

    @Mock
    private UpdateOrdemStatusUseCase updateOrdemStatusUseCase;

    @InjectMocks
    private EvaluateOrdemUseCaseImpl evaluateOSService;

    @Test
    public void executeSuccess() throws GatewayException, ValidationException, UseCaseException {
        Long osId = 1L;

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.recebida(osId));

        doAnswer(invocation -> {
            Ordem osArg = invocation.getArgument(0);
            Status status = invocation.getArgument(1);
            osArg.setStatus(status);
            return null;
        }).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));

        when(updateOrdemGateway.execute(any(Ordem.class))).thenReturn(OrdemDeServicoFixture.emDiagnostico(osId));

        Ordem result = evaluateOSService.execute(osId);

        verify(findOrdemGateway, times(1)).execute(any(Long.class));
        verify(updateOrdemStatusUseCase, times(1)).execute(any(Ordem.class), any(Status.class));
        verify(updateOrdemGateway, times(1)).execute(any(Ordem.class));
        verifyNoMoreInteractions(findOrdemGateway);
        verifyNoMoreInteractions(updateOrdemStatusUseCase);
        verifyNoMoreInteractions(updateOrdemGateway);

        assertEquals(Status.EM_DIAGNOSTICO, result.getStatus());
    }

    @Test
    public void onGatewayException() throws GatewayException {
        Long osId = 1L;

        doThrow(GatewayException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> {
            evaluateOSService.execute(osId);
        });
    }

    @Test
    public void onValidationException() throws GatewayException, ValidationException, UseCaseException {
        Long osId = 1L;

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.recebida(osId));
        doThrow(ValidationException.class).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));

        assertThrows(ValidationException.class, () -> {
            evaluateOSService.execute(osId);
        });
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        Long osId = 1L;

        doThrow(RuntimeException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(UseCaseException.class, () -> {
            evaluateOSService.execute(osId);
        });
    }
}