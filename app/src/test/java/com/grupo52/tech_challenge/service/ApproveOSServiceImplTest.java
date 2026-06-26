package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.service.impl.ApproveOSServiceImpl;
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
class ApproveOSServiceImplTest {

    @Mock
    private FindOSGateway findOSGateway;

    @Mock
    private UpdateOSStatusService updateOSStatusService;

    @Mock
    private CalculateOSPriceService calculateOSPriceService;

    @InjectMocks
    private ApproveOSServiceImpl approveOSService;

    @Test
    public void approveAllSuccess() throws GatewayException, ValidationException, ServiceException {
        Long osId = 1L;
        OrdemDeServico os = OrdemDeServicoFixture.aguardandoAprovacao(osId);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            OrdemDeServico osArg = invocation.getArgument(0);
            StatusOS status = invocation.getArgument(1);
            osArg.setStatus(status);
            return null;
        }).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(calculateOSPriceService.calculateApprovedPrice(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemDeServico result = approveOSService.approveAll(osId);

        assertTrue(result.getServicosDesejados().stream().allMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
        assertTrue(result.getServicosNecessarios().stream().allMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
        assertTrue(result.getServicosAdicionais().stream().allMatch(s -> Boolean.TRUE.equals(s.getAprovado())));

        verify(findOSGateway, times(1)).execute(any(Long.class));
        verify(updateOSStatusService, times(1)).execute(os, StatusOS.APROVADA);
        verify(calculateOSPriceService, times(1)).calculateApprovedPrice(os);
        verifyNoMoreInteractions(findOSGateway, updateOSStatusService, calculateOSPriceService);
    }

    @Test
    public void parcialApproveSuccess() throws GatewayException, ValidationException, ServiceException {
        Long osId = 1L;
        OrdemDeServico os = OrdemDeServicoFixture.aguardandoAprovacaoComIds(osId);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            OrdemDeServico osArg = invocation.getArgument(0);
            StatusOS status = invocation.getArgument(1);
            osArg.setStatus(status);
            return null;
        }).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(calculateOSPriceService.calculateApprovedPrice(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Long> servicosAprovados = List.of(1L, 3L);

        OrdemDeServico result = approveOSService.parcialApprove(osId, servicosAprovados);

        assertTrue(result.getServicosDesejados().stream().filter(s -> s.getId().equals(1L)).allMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
        assertFalse(result.getServicosDesejados().stream().filter(s -> s.getId().equals(2L)).allMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
        assertTrue(result.getServicosNecessarios().stream().filter(s -> s.getId().equals(3L)).allMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
        assertFalse(result.getServicosAdicionais().stream().filter(s -> s.getId().equals(4L)).allMatch(s -> Boolean.TRUE.equals(s.getAprovado())));

        verify(findOSGateway, times(1)).execute(any(Long.class));
        verify(updateOSStatusService, times(1)).execute(os, StatusOS.APROVADA);
        verify(calculateOSPriceService, times(1)).calculateApprovedPrice(os);
        verifyNoMoreInteractions(findOSGateway, updateOSStatusService, calculateOSPriceService);
    }

    @Test
    public void parcialApproveWithEmptyListApprovesNone() throws GatewayException, ValidationException, ServiceException {
        Long osId = 1L;
        OrdemDeServico os = OrdemDeServicoFixture.aguardandoAprovacaoComIds(osId);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            OrdemDeServico osArg = invocation.getArgument(0);
            osArg.setStatus(invocation.getArgument(1));
            return null;
        }).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(calculateOSPriceService.calculateApprovedPrice(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemDeServico result = approveOSService.parcialApprove(osId, List.of());

        assertTrue(result.getServicosDesejados().stream().noneMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
        assertTrue(result.getServicosNecessarios().stream().noneMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
        assertTrue(result.getServicosAdicionais().stream().noneMatch(s -> Boolean.TRUE.equals(s.getAprovado())));
    }

    @Test
    public void approveAllOnGatewayException() throws GatewayException {
        doThrow(GatewayException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> approveOSService.approveAll(1L));
    }

    @Test
    public void approveAllOnValidationException() throws GatewayException, ValidationException, ServiceException {
        OrdemDeServico os = OrdemDeServicoFixture.aguardandoAprovacao(1L);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doThrow(ValidationException.class).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));

        assertThrows(ValidationException.class, () -> approveOSService.approveAll(1L));
    }

    @Test
    public void approveAllOnUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(ServiceException.class, () -> approveOSService.approveAll(1L));
    }

    @Test
    public void parcialApproveOnGatewayException() throws GatewayException {
        doThrow(GatewayException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> approveOSService.parcialApprove(1L, List.of(1L)));
    }

    @Test
    public void parcialApproveOnValidationException() throws GatewayException, ValidationException, ServiceException {
        OrdemDeServico os = OrdemDeServicoFixture.aguardandoAprovacaoComIds(1L);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doThrow(ValidationException.class).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));

        assertThrows(ValidationException.class, () -> approveOSService.parcialApprove(1L, List.of(1L)));
    }

    @Test
    public void parcialApproveOnUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(ServiceException.class, () -> approveOSService.parcialApprove(1L, List.of(1L)));
    }
}