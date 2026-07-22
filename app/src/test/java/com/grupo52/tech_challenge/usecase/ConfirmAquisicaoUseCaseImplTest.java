package com.grupo52.tech_challenge.usecase;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemInsumo;
import com.grupo52.tech_challenge.domain.OrdemPeca;
import com.grupo52.tech_challenge.domain.OrdemServico;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.gateway.UpdateInsumoGateway;
import com.grupo52.tech_challenge.gateway.UpdateOrdemGateway;
import com.grupo52.tech_challenge.gateway.UpdatePecaGateway;
import com.grupo52.tech_challenge.usecase.impl.ConfirmAquisicaoUseCaseImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmAquisicaoUseCaseImplTest {

    @Mock
    private FindOrdemGateway findOrdemGateway;

    @Mock
    private UpdateOrdemStatusUseCase updateOrdemStatusUseCase;

    @Mock
    private UpdateOrdemGateway updateOrdemGateway;

    @Mock
    private UpdatePecaGateway updatePecaGateway;

    @Mock
    private UpdateInsumoGateway updateInsumoGateway;

    @InjectMocks
    private ConfirmAquisicaoUseCaseImpl confirmAquisicaoService;

    private Ordem osAguardandoAquisicao(Peca peca, Insumo insumo) {
        OrdemServico servico = OrdemServico.builder()
                .servico(Servico.builder().nome("Troca de disco").build())
                .precoTotal(new BigDecimal("205.00"))
                .aprovado(true)
                .pecas(new ArrayList<>(List.of(
                        OrdemPeca.builder().peca(peca).quantidade(2).precoTotal(new BigDecimal("180.00")).reservado(false).build()
                )))
                .insumos(new ArrayList<>(List.of(
                        OrdemInsumo.builder().insumo(insumo).quantidade(1).precoTotal(new BigDecimal("25.00")).reservado(false).build()
                )))
                .build();

        return Ordem.builder()
                .id(1L)
                .status(Status.AGUARDANDO_AQUISICAO)
                .servicosDesejados(new ArrayList<>(List.of(servico)))
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();
    }

    @Test
    public void executeSucesso() throws GatewayException, UseCaseException, ValidationException {
        Peca peca = Peca.builder().id(100L).nome("Disco de freio").preco(new BigDecimal("90.00")).estoque(0).estoqueReservado(0).build();
        Insumo insumo = Insumo.builder().id(200L).nome("Fluido de freio").preco(new BigDecimal("25.00")).estoque(0).estoqueReservado(0).build();
        Ordem os = osAguardandoAquisicao(peca, insumo);

        when(findOrdemGateway.execute(1L)).thenReturn(os);
        doAnswer(invocation -> {
            Ordem osArg = invocation.getArgument(0);
            osArg.setStatus(invocation.getArgument(1));
            return null;
        }).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updateInsumoGateway.execute(any(Insumo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = confirmAquisicaoService.execute(1L);

        assertEquals(Status.APROVADA, result.getStatus());
        assertEquals(2, peca.getEstoque());
        assertEquals(2, peca.getEstoqueReservado());
        assertEquals(1, insumo.getEstoque());
        assertEquals(1, insumo.getEstoqueReservado());
        assertTrue(result.getServicosDesejados().getFirst().getPecas().getFirst().getReservado());
        assertTrue(result.getServicosDesejados().getFirst().getInsumos().getFirst().getReservado());
        verify(updatePecaGateway, times(1)).execute(peca);
        verify(updateInsumoGateway, times(1)).execute(insumo);
        verify(updateOrdemStatusUseCase, times(1)).execute(os, Status.APROVADA);
        verify(updateOrdemGateway, times(1)).execute(os);
    }

    @Test
    public void executeSemItensParaAdquirirApenasAprova() throws GatewayException, UseCaseException, ValidationException {
        OrdemServico servico = OrdemServico.builder()
                .servico(Servico.builder().nome("Balanceamento").build())
                .precoTotal(BigDecimal.TEN)
                .aprovado(true)
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>())
                .build();

        Ordem os = Ordem.builder()
                .id(1L)
                .status(Status.AGUARDANDO_AQUISICAO)
                .servicosDesejados(new ArrayList<>(List.of(servico)))
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();

        when(findOrdemGateway.execute(1L)).thenReturn(os);
        doAnswer(invocation -> {
            Ordem osArg = invocation.getArgument(0);
            osArg.setStatus(invocation.getArgument(1));
            return null;
        }).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = confirmAquisicaoService.execute(1L);

        assertEquals(Status.APROVADA, result.getStatus());
        verifyNoInteractions(updatePecaGateway, updateInsumoGateway);
    }

    @Test
    public void onGatewayException() throws GatewayException {
        doThrow(GatewayException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> confirmAquisicaoService.execute(1L));
    }

    @Test
    public void onValidationException() throws GatewayException, ValidationException, UseCaseException {
        Peca peca = Peca.builder().id(100L).nome("Disco de freio").preco(new BigDecimal("90.00")).estoque(0).estoqueReservado(0).build();
        Insumo insumo = Insumo.builder().id(200L).nome("Fluido de freio").preco(new BigDecimal("25.00")).estoque(0).estoqueReservado(0).build();
        Ordem os = osAguardandoAquisicao(peca, insumo);

        when(findOrdemGateway.execute(1L)).thenReturn(os);
        doThrow(ValidationException.class).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));

        assertThrows(ValidationException.class, () -> confirmAquisicaoService.execute(1L));

        verifyNoInteractions(updateOrdemGateway);
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(UseCaseException.class, () -> confirmAquisicaoService.execute(1L));
    }
}