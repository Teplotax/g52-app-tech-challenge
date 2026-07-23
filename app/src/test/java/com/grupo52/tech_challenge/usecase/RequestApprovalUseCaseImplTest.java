package com.grupo52.tech_challenge.usecase;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.gateway.SendOrcamentoEmailGateway;
import com.grupo52.tech_challenge.gateway.UpdateOrdemGateway;
import com.grupo52.tech_challenge.usecase.impl.RequestApprovalUseCaseImpl;
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
class RequestApprovalUseCaseImplTest {

    @Mock
    private FindOrdemGateway findOrdemGateway;

    @Mock
    private UpdateOrdemGateway updateOrdemGateway;

    @Mock
    private UpdateOrdemStatusUseCase updateOrdemStatusUseCase;

    @Mock
    private SendOrcamentoEmailGateway sendOrcamentoEmailGateway;

    @InjectMocks
    private RequestApprovalUseCaseImpl requestApprovalService;

    @Test
    public void executeSuccess() throws GatewayException, ValidationException, UseCaseException {
        Long osId = 1L;
        Ordem os = OrdemDeServicoFixture.emDiagnostico(osId);
        Ordem updated = OrdemDeServicoFixture.aguardandoAprovacao(osId);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            Ordem osArg = invocation.getArgument(0);
            osArg.setStatus(invocation.getArgument(1));
            return null;
        }).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenReturn(updated);

        Ordem result = requestApprovalService.execute(osId);

        assertEquals(Status.AGUARDANDO_APROVACAO, result.getStatus());

        verify(findOrdemGateway, times(1)).execute(osId);
        verify(updateOrdemStatusUseCase, times(1)).execute(os, Status.AGUARDANDO_APROVACAO);
        verify(updateOrdemGateway, times(1)).execute(os);
        verify(sendOrcamentoEmailGateway, times(1)).execute(updated);
        verifyNoMoreInteractions(findOrdemGateway, updateOrdemStatusUseCase, updateOrdemGateway, sendOrcamentoEmailGateway);
    }

    @Test
    public void onGatewayExceptionFromFind() throws GatewayException {
        doThrow(GatewayException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> requestApprovalService.execute(1L));
    }

    @Test
    public void onValidationException() throws GatewayException, ValidationException, UseCaseException {
        Ordem os = OrdemDeServicoFixture.emDiagnostico(1L);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doThrow(ValidationException.class).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));

        assertThrows(ValidationException.class, () -> requestApprovalService.execute(1L));

        verify(sendOrcamentoEmailGateway, never()).execute(any());
    }

    @Test
    public void onGatewayExceptionFromEmail() throws GatewayException, ValidationException, UseCaseException {
        Long osId = 1L;
        Ordem os = OrdemDeServicoFixture.emDiagnostico(osId);
        Ordem updated = OrdemDeServicoFixture.aguardandoAprovacao(osId);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doNothing().when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenReturn(updated);
        doThrow(GatewayException.class).when(sendOrcamentoEmailGateway).execute(any(Ordem.class));

        assertThrows(GatewayException.class, () -> requestApprovalService.execute(osId));
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(UseCaseException.class, () -> requestApprovalService.execute(1L));
    }
}