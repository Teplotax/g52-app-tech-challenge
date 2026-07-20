package com.grupo52.tech_challenge.usecase;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.usecase.impl.ApproveOrdemUseCaseImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveOrdemUseCaseImplTest {

    @Mock
    private FindOrdemGateway findOrdemGateway;

    @Mock
    private UpdateOrdemStatusUseCase updateOrdemStatusUseCase;

    @Mock
    private CalculateOrdemPriceUseCase calculateOrdemPriceUseCase;

    @InjectMocks
    private ApproveOrdemUseCaseImpl approveOSService;

    @Test
    public void approveAllSuccess() throws GatewayException, ValidationException, UseCaseException {
        Long osId = 1L;
        Ordem os = OrdemDeServicoFixture.aguardandoAprovacao(osId);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            Ordem osArg = invocation.getArgument(0);
            Status status = invocation.getArgument(1);
            osArg.setStatus(status);
            return null;
        }).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(calculateOrdemPriceUseCase.calculateApprovedPrice(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = approveOSService.approveAll(osId);

        assertTrue(result.getServicosDesejados().stream().allMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
        assertTrue(result.getServicosNecessarios().stream().allMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
        assertTrue(result.getServicosAdicionais().stream().allMatch(s -> Boolean.TRUE.equals(s.getAprovado())));

        verify(findOrdemGateway, times(1)).execute(any(Long.class));
        verify(updateOrdemStatusUseCase, times(1)).execute(os, Status.APROVADA);
        verify(calculateOrdemPriceUseCase, times(1)).calculateApprovedPrice(os);
        verifyNoMoreInteractions(findOrdemGateway, updateOrdemStatusUseCase, calculateOrdemPriceUseCase);
    }

    @Test
    public void parcialApproveSuccess() throws GatewayException, ValidationException, UseCaseException {
        Long osId = 1L;
        Ordem os = OrdemDeServicoFixture.aguardandoAprovacaoComIds(osId);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            Ordem osArg = invocation.getArgument(0);
            Status status = invocation.getArgument(1);
            osArg.setStatus(status);
            return null;
        }).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(calculateOrdemPriceUseCase.calculateApprovedPrice(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Long> servicosAprovados = List.of(1L, 3L);

        Ordem result = approveOSService.parcialApprove(osId, servicosAprovados);

        assertTrue(result.getServicosDesejados().stream().filter(s -> s.getId().equals(1L)).allMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
        assertFalse(result.getServicosDesejados().stream().filter(s -> s.getId().equals(2L)).allMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
        assertTrue(result.getServicosNecessarios().stream().filter(s -> s.getId().equals(3L)).allMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
        assertFalse(result.getServicosAdicionais().stream().filter(s -> s.getId().equals(4L)).allMatch(s -> Boolean.TRUE.equals(s.getAprovado())));

        verify(findOrdemGateway, times(1)).execute(any(Long.class));
        verify(updateOrdemStatusUseCase, times(1)).execute(os, Status.APROVADA);
        verify(calculateOrdemPriceUseCase, times(1)).calculateApprovedPrice(os);
        verifyNoMoreInteractions(findOrdemGateway, updateOrdemStatusUseCase, calculateOrdemPriceUseCase);
    }

    @Test
    public void parcialApproveWithEmptyListApprovesNone() throws GatewayException, ValidationException, UseCaseException {
        Long osId = 1L;
        Ordem os = OrdemDeServicoFixture.aguardandoAprovacaoComIds(osId);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            Ordem osArg = invocation.getArgument(0);
            osArg.setStatus(invocation.getArgument(1));
            return null;
        }).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(calculateOrdemPriceUseCase.calculateApprovedPrice(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = approveOSService.parcialApprove(osId, List.of());

        assertTrue(result.getServicosDesejados().stream().noneMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
        assertTrue(result.getServicosNecessarios().stream().noneMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
        assertTrue(result.getServicosAdicionais().stream().noneMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
    }

    @Test
    public void approveAllOnGatewayException() throws GatewayException {
        doThrow(GatewayException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> approveOSService.approveAll(1L));
    }

    @Test
    public void approveAllOnValidationException() throws GatewayException, ValidationException, UseCaseException {
        Ordem os = OrdemDeServicoFixture.aguardandoAprovacao(1L);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doThrow(ValidationException.class).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));

        assertThrows(ValidationException.class, () -> approveOSService.approveAll(1L));
    }

    @Test
    public void approveAllOnUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(UseCaseException.class, () -> approveOSService.approveAll(1L));
    }

    @Test
    public void parcialApproveOnGatewayException() throws GatewayException {
        doThrow(GatewayException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> approveOSService.parcialApprove(1L, List.of(1L)));
    }

    @Test
    public void parcialApproveOnValidationException() throws GatewayException, ValidationException, UseCaseException {
        Ordem os = OrdemDeServicoFixture.aguardandoAprovacaoComIds(1L);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doThrow(ValidationException.class).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));

        assertThrows(ValidationException.class, () -> approveOSService.parcialApprove(1L, List.of(1L)));
    }

    @Test
    public void parcialApproveOnUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(UseCaseException.class, () -> approveOSService.parcialApprove(1L, List.of(1L)));
    }
}