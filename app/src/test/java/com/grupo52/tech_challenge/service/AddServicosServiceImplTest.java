package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.domain.ServicoOS;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.UpdateOSGateway;
import com.grupo52.tech_challenge.service.impl.AddServicosServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddServicosServiceImplTest {

    @Mock
    private FindOSGateway findOSGateway;

    @Mock
    private UpdateOSGateway updateOSGateway;

    @Mock
    private CalculateOSPriceService calculateOSPriceService;

    @Mock
    private UpdateOSStatusService updateOSStatusService;

    @InjectMocks
    private AddServicosServiceImpl addServicosService;

    @Test
    public void executeSuccessFromRecebida() throws GatewayException, ValidationException, ServiceException {
        Long osId = 1L;

        OrdemDeServico savedOS = OrdemDeServico.builder()
                .id(osId)
                .status(StatusOS.RECEBIDA)
                .cliente(OrdemDeServicoFixture.clienteJoaoSilva())
                .veiculo(OrdemDeServicoFixture.veiculoCorollaABC1D23())
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();

        OrdemDeServico novosServicos = OrdemDeServico.builder()
                .id(osId)
                .justificativaNecessarios("Disco de freio desgastado")
                .justificativaAdicionais("Lâmpada de farol queimada")
                .servicosNecessarios(List.of(ServicoOS.builder().servico(Servico.builder().id(5L).build()).build()))
                .servicosAdicionais(List.of(ServicoOS.builder().servico(Servico.builder().id(9L).build()).build()))
                .build();

        OrdemDeServico calculatedOS = OrdemDeServicoFixture.emDiagnostico(osId);

        when(findOSGateway.execute(any(Long.class))).thenReturn(savedOS);
        doAnswer(invocation -> {
            OrdemDeServico osArg = invocation.getArgument(0);
            StatusOS status = invocation.getArgument(1);
            osArg.setStatus(status);
            return null;
        }).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(calculateOSPriceService.calculateServicosNecessarios(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(calculateOSPriceService.calculateServicosAdicionais(any(OrdemDeServico.class))).thenReturn(calculatedOS);

        OrdemDeServico result = addServicosService.execute(novosServicos);

        assertEquals(StatusOS.EM_DIAGNOSTICO, savedOS.getStatus());
        assertEquals(1, savedOS.getServicosNecessarios().size());
        assertEquals(1, savedOS.getServicosAdicionais().size());
        assertEquals("Disco de freio desgastado", savedOS.getJustificativaNecessarios());
        assertEquals("Lâmpada de farol queimada", savedOS.getJustificativaAdicionais());
        assertEquals(calculatedOS, result);

        verify(findOSGateway, times(1)).execute(any(Long.class));
        verify(updateOSStatusService, times(1)).execute(any(OrdemDeServico.class), any(StatusOS.class));
        verify(updateOSGateway, times(1)).execute(any(OrdemDeServico.class));
        verify(calculateOSPriceService, times(1)).calculateServicosNecessarios(any(OrdemDeServico.class));
        verify(calculateOSPriceService, times(1)).calculateServicosAdicionais(any(OrdemDeServico.class));
    }

    @Test
    public void executeSuccessAppendingExistingJustificativa() throws GatewayException, ValidationException, ServiceException {
        Long osId = 1L;

        OrdemDeServico savedOS = OrdemDeServico.builder()
                .id(osId)
                .status(StatusOS.EM_DIAGNOSTICO)
                .justificativaNecessarios("Pastilha de freio gasta")
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();

        OrdemDeServico novosServicos = OrdemDeServico.builder()
                .id(osId)
                .justificativaNecessarios("Disco de freio empenado")
                .servicosNecessarios(List.of())
                .servicosAdicionais(List.of())
                .build();

        when(findOSGateway.execute(any(Long.class))).thenReturn(savedOS);
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(calculateOSPriceService.calculateServicosNecessarios(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(calculateOSPriceService.calculateServicosAdicionais(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        addServicosService.execute(novosServicos);

        assertEquals("Pastilha de freio gasta \n Disco de freio empenado", savedOS.getJustificativaNecessarios());
        verifyNoInteractions(updateOSStatusService);
    }

    @Test
    public void onInvalidStatus() throws GatewayException {
        Long osId = 1L;

        OrdemDeServico savedOS = OrdemDeServico.builder()
                .id(osId)
                .status(StatusOS.CANCELADA)
                .build();

        when(findOSGateway.execute(any(Long.class))).thenReturn(savedOS);

        assertThrows(InvalidStatusException.class, () -> {
            addServicosService.execute(OrdemDeServico.builder().id(osId).build());
        });
    }

    @Test
    public void onGatewayException() throws GatewayException {
        doThrow(GatewayException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> {
            addServicosService.execute(OrdemDeServico.builder().id(1L).build());
        });
    }

    @Test
    public void onValidationExceptionFromStatusService() throws GatewayException, ValidationException, ServiceException {
        OrdemDeServico savedOS = OrdemDeServico.builder()
                .id(1L)
                .status(StatusOS.RECEBIDA)
                .build();

        when(findOSGateway.execute(any(Long.class))).thenReturn(savedOS);
        doThrow(ValidationException.class).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));

        assertThrows(ValidationException.class, () -> {
            addServicosService.execute(OrdemDeServico.builder().id(1L).build());
        });
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(ServiceException.class, () -> {
            addServicosService.execute(OrdemDeServico.builder().id(1L).build());
        });
    }
}