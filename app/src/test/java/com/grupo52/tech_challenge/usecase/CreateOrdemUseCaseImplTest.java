package com.grupo52.tech_challenge.usecase;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemServico;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.CreateOrdemGateway;
import com.grupo52.tech_challenge.usecase.impl.CreateOrdemUseCaseImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrdemUseCaseImplTest {

    @Mock
    private CreateOrdemGateway createOrdemGateway;

    @Mock
    private CalculateOrdemPriceUseCase calculateOrdemPriceUseCase;

    @InjectMocks
    private CreateOrdemUseCaseImpl createOrdemService;

    @Test
    public void executeSuccess() throws GatewayException, UseCaseException {
        Ordem novaOS = Ordem.builder()
                .veiculo(OrdemDeServicoFixture.veiculoCorollaABC1D23())
                .tagChave("001")
                .status(Status.RECEBIDA)
                .servicosDesejados(List.of(OrdemServico.builder()
                        .servico(Servico.builder().id(1L).build())
                        .aprovado(false)
                        .build()))
                .build();

        Ordem savedOS = OrdemDeServicoFixture.recebida(1L);
        Ordem calculatedOS = OrdemDeServicoFixture.recebida(1L);

        when(createOrdemGateway.execute(novaOS)).thenReturn(savedOS);
        when(calculateOrdemPriceUseCase.calculateServicosDesejados(savedOS)).thenReturn(calculatedOS);

        Ordem result = createOrdemService.execute(novaOS);

        assertEquals(calculatedOS, result);

        verify(createOrdemGateway, times(1)).execute(novaOS);
        verify(calculateOrdemPriceUseCase, times(1)).calculateServicosDesejados(savedOS);
        verifyNoMoreInteractions(createOrdemGateway, calculateOrdemPriceUseCase);
    }

    @Test
    public void onGatewayExceptionFromCreate() throws GatewayException, UseCaseException {
        Ordem novaOS = Ordem.builder().status(Status.RECEBIDA).build();

        doThrow(GatewayException.class).when(createOrdemGateway).execute(novaOS);

        assertThrows(GatewayException.class, () -> createOrdemService.execute(novaOS));

        verifyNoInteractions(calculateOrdemPriceUseCase);
    }

    @Test
    public void onUseCaseExceptionFromCreate() throws GatewayException, UseCaseException {
        Ordem novaOS = Ordem.builder().status(Status.RECEBIDA).build();

        doThrow(UseCaseException.class).when(createOrdemGateway).execute(novaOS);

        assertThrows(UseCaseException.class, () -> createOrdemService.execute(novaOS));

        verifyNoInteractions(calculateOrdemPriceUseCase);
    }

    @Test
    public void onGatewayExceptionFromCalculatePrice() throws GatewayException, UseCaseException {
        Ordem novaOS = Ordem.builder().status(Status.RECEBIDA).build();
        Ordem savedOS = OrdemDeServicoFixture.recebida(1L);

        when(createOrdemGateway.execute(novaOS)).thenReturn(savedOS);
        doThrow(GatewayException.class).when(calculateOrdemPriceUseCase).calculateServicosDesejados(savedOS);

        assertThrows(GatewayException.class, () -> createOrdemService.execute(novaOS));
    }

    @Test
    public void onUseCaseExceptionFromCalculatePrice() throws GatewayException, UseCaseException {
        Ordem novaOS = Ordem.builder().status(Status.RECEBIDA).build();
        Ordem savedOS = OrdemDeServicoFixture.recebida(1L);

        when(createOrdemGateway.execute(novaOS)).thenReturn(savedOS);
        doThrow(UseCaseException.class).when(calculateOrdemPriceUseCase).calculateServicosDesejados(savedOS);

        assertThrows(UseCaseException.class, () -> createOrdemService.execute(novaOS));
    }

    @Test
    public void onUnexpectedException() throws GatewayException, UseCaseException {
        Ordem novaOS = Ordem.builder().status(Status.RECEBIDA).build();

        doThrow(RuntimeException.class).when(createOrdemGateway).execute(novaOS);

        assertThrows(UseCaseException.class, () -> createOrdemService.execute(novaOS));

        verifyNoInteractions(calculateOrdemPriceUseCase);
    }
}