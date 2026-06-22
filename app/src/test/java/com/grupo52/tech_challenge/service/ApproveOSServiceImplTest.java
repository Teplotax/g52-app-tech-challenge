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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApproveOSServiceImplTest {

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
    public void onGatewayException() throws GatewayException {
        doThrow(GatewayException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> {
            approveOSService.approveAll(1L);
        });
    }

    @Test
    public void onValidationException() throws GatewayException, ValidationException, ServiceException {
        OrdemDeServico os = OrdemDeServicoFixture.aguardandoAprovacao(1L);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doThrow(ValidationException.class).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));

        assertThrows(ValidationException.class, () -> {
            approveOSService.approveAll(1L);
        });
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(ServiceException.class, () -> {
            approveOSService.approveAll(1L);
        });
    }
}