package com.grupo52.tech_challenge.usecase;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.domain.OrdemServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.gateway.UpdateOrdemGateway;
import com.grupo52.tech_challenge.usecase.impl.AddServicosUseCaseImpl;
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
class AddServicosUseCaseImplTest {

    @Mock
    private FindOrdemGateway findOrdemGateway;

    @Mock
    private UpdateOrdemGateway updateOrdemGateway;

    @Mock
    private CalculateOrdemPriceUseCase calculateOrdemPriceUseCase;

    @Mock
    private UpdateOrdemStatusUseCase updateOrdemStatusUseCase;

    @InjectMocks
    private AddServicosUseCaseImpl addServicosService;

    @Test
    public void executeSuccessFromRecebida() throws GatewayException, ValidationException, UseCaseException {
        Long osId = 1L;

        Ordem savedOS = Ordem.builder()
                .id(osId)
                .status(Status.RECEBIDA)
                .cliente(OrdemDeServicoFixture.clienteJoaoSilva())
                .veiculo(OrdemDeServicoFixture.veiculoCorollaABC1D23())
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();

        Ordem novosServicos = Ordem.builder()
                .id(osId)
                .justificativaNecessarios("Disco de freio desgastado")
                .justificativaAdicionais("Lâmpada de farol queimada")
                .servicosNecessarios(List.of(OrdemServico.builder().servico(Servico.builder().id(5L).build()).build()))
                .servicosAdicionais(List.of(OrdemServico.builder().servico(Servico.builder().id(9L).build()).build()))
                .build();

        Ordem calculatedOS = OrdemDeServicoFixture.emDiagnostico(osId);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(savedOS);
        doAnswer(invocation -> {
            Ordem osArg = invocation.getArgument(0);
            Status status = invocation.getArgument(1);
            osArg.setStatus(status);
            return null;
        }).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(calculateOrdemPriceUseCase.calculateServicosNecessarios(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(calculateOrdemPriceUseCase.calculateServicosAdicionais(any(Ordem.class))).thenReturn(calculatedOS);

        Ordem result = addServicosService.execute(novosServicos);

        assertEquals(Status.EM_DIAGNOSTICO, savedOS.getStatus());
        assertEquals(1, savedOS.getServicosNecessarios().size());
        assertEquals(1, savedOS.getServicosAdicionais().size());
        assertEquals("Disco de freio desgastado", savedOS.getJustificativaNecessarios());
        assertEquals("Lâmpada de farol queimada", savedOS.getJustificativaAdicionais());
        assertEquals(calculatedOS, result);

        verify(findOrdemGateway, times(1)).execute(any(Long.class));
        verify(updateOrdemStatusUseCase, times(1)).execute(any(Ordem.class), any(Status.class));
        verify(updateOrdemGateway, times(1)).execute(any(Ordem.class));
        verify(calculateOrdemPriceUseCase, times(1)).calculateServicosNecessarios(any(Ordem.class));
        verify(calculateOrdemPriceUseCase, times(1)).calculateServicosAdicionais(any(Ordem.class));
    }

    @Test
    public void executeSuccessAppendingExistingJustificativa() throws GatewayException, ValidationException, UseCaseException {
        Long osId = 1L;

        Ordem savedOS = Ordem.builder()
                .id(osId)
                .status(Status.EM_DIAGNOSTICO)
                .justificativaNecessarios("Pastilha de freio gasta")
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();

        Ordem novosServicos = Ordem.builder()
                .id(osId)
                .justificativaNecessarios("Disco de freio empenado")
                .servicosNecessarios(List.of())
                .servicosAdicionais(List.of())
                .build();

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(savedOS);
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(calculateOrdemPriceUseCase.calculateServicosNecessarios(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(calculateOrdemPriceUseCase.calculateServicosAdicionais(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        addServicosService.execute(novosServicos);

        assertEquals("Pastilha de freio gasta \n Disco de freio empenado", savedOS.getJustificativaNecessarios());
        verifyNoInteractions(updateOrdemStatusUseCase);
    }

    @Test
    public void onInvalidStatus() throws GatewayException {
        Long osId = 1L;

        Ordem savedOS = Ordem.builder()
                .id(osId)
                .status(Status.CANCELADA)
                .build();

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(savedOS);

        assertThrows(InvalidStatusException.class, () -> {
            addServicosService.execute(Ordem.builder().id(osId).build());
        });
    }

    @Test
    public void onGatewayException() throws GatewayException {
        doThrow(GatewayException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> {
            addServicosService.execute(Ordem.builder().id(1L).build());
        });
    }

    @Test
    public void onValidationExceptionFromStatusService() throws GatewayException, ValidationException, UseCaseException {
        Ordem savedOS = Ordem.builder()
                .id(1L)
                .status(Status.RECEBIDA)
                .build();

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(savedOS);
        doThrow(ValidationException.class).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));

        assertThrows(ValidationException.class, () -> {
            addServicosService.execute(Ordem.builder().id(1L).build());
        });
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(UseCaseException.class, () -> {
            addServicosService.execute(Ordem.builder().id(1L).build());
        });
    }
}