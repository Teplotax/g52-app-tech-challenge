package com.grupo52.tech_challenge.usecase;

import com.grupo52.tech_challenge.domain.*;
import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.gateway.UpdateInsumoGateway;
import com.grupo52.tech_challenge.gateway.UpdateOrdemGateway;
import com.grupo52.tech_challenge.gateway.UpdatePecaGateway;
import com.grupo52.tech_challenge.usecase.impl.FinalizeOrdemUseCaseImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinalizeOrdemUseCaseImplTest {

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
    private FinalizeOrdemUseCaseImpl finalizeOSService;

    @Test
    public void executeSuccess() throws GatewayException, ValidationException, UseCaseException {
        Long osId = 1L;
        Ordem os = osEmExecucao(List.of(), List.of(), List.of());

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            Ordem osArg = invocation.getArgument(0);
            osArg.setStatus(invocation.getArgument(1));
            return null;
        }).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenReturn(os);

        Ordem result = finalizeOSService.execute(osId);

        assertEquals(Status.FINALIZADA, result.getStatus());
        verify(findOrdemGateway, times(1)).execute(osId);
        verify(updateOrdemStatusUseCase, times(1)).execute(os, Status.FINALIZADA);
        verify(updateOrdemGateway, times(1)).execute(os);
        verifyNoInteractions(updatePecaGateway, updateInsumoGateway);
    }

    @Test
    public void consumesPecaAndInsumoForApprovedServicos() throws GatewayException, ValidationException, UseCaseException {
        Peca peca = pecaComEstoque(10, 4);
        Insumo insumo = insumoComEstoque(5, 1);
        OrdemServico servicoAprovado = servicoComPecaInsumo(peca, 4, insumo, 1, true);

        Ordem os = osEmExecucao(List.of(servicoAprovado), List.of(), List.of());

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doNothing().when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updateInsumoGateway.execute(any(Insumo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        finalizeOSService.execute(1L);

        assertEquals(0, peca.getEstoqueReservado());
        assertEquals(6, peca.getEstoque());
        assertEquals(0, insumo.getEstoqueReservado());
        assertEquals(4, insumo.getEstoque());
        verify(updatePecaGateway, times(1)).execute(peca);
        verify(updateInsumoGateway, times(1)).execute(insumo);
    }

    @Test
    public void skipsNonApprovedServicos() throws GatewayException, ValidationException, UseCaseException {
        Peca peca = pecaComEstoque(10, 4);
        OrdemServico servicoRecusado = servicoSoPecas(peca, 4, false);

        Ordem os = osEmExecucao(List.of(servicoRecusado), List.of(), List.of());

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doNothing().when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        finalizeOSService.execute(1L);

        assertEquals(4, peca.getEstoqueReservado());
        assertEquals(10, peca.getEstoque());
        verifyNoInteractions(updatePecaGateway, updateInsumoGateway);
    }

    @Test
    public void consumesAcrossAllThreeLists() throws GatewayException, ValidationException, UseCaseException {
        Peca pecaDesejado = pecaComEstoque(10, 2);
        Peca pecaNecessario = pecaComEstoque(8, 1);
        Peca pecaAdicional = pecaComEstoque(5, 1);

        Ordem os = osEmExecucao(
                List.of(servicoSoPecas(pecaDesejado, 2, true)),
                List.of(servicoSoPecas(pecaNecessario, 1, true)),
                List.of(servicoSoPecas(pecaAdicional, 1, true))
        );

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doNothing().when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        finalizeOSService.execute(1L);

        assertEquals(0, pecaDesejado.getEstoqueReservado());
        assertEquals(8, pecaDesejado.getEstoque());
        assertEquals(0, pecaNecessario.getEstoqueReservado());
        assertEquals(7, pecaNecessario.getEstoque());
        assertEquals(0, pecaAdicional.getEstoqueReservado());
        assertEquals(4, pecaAdicional.getEstoque());
        verify(updatePecaGateway, times(3)).execute(any(Peca.class));
    }

    @Test
    public void onGatewayException() throws GatewayException {
        doThrow(GatewayException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> finalizeOSService.execute(1L));
    }

    @Test
    public void onValidationException() throws GatewayException, ValidationException, UseCaseException {
        Ordem os = osEmExecucao(List.of(), List.of(), List.of());

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doThrow(ValidationException.class).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));

        assertThrows(ValidationException.class, () -> finalizeOSService.execute(1L));

        verifyNoInteractions(updatePecaGateway, updateInsumoGateway, updateOrdemGateway);
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(UseCaseException.class, () -> finalizeOSService.execute(1L));
    }

    private Ordem osEmExecucao(List<OrdemServico> desejados, List<OrdemServico> necessarios, List<OrdemServico> adicionais) {
        return Ordem.builder()
                .id(1L)
                .status(Status.EM_EXECUCAO)
                .cliente(OrdemDeServicoFixture.clienteJoaoSilva())
                .veiculo(OrdemDeServicoFixture.veiculoCorollaABC1D23())
                .servicosDesejados(new ArrayList<>(desejados))
                .servicosNecessarios(new ArrayList<>(necessarios))
                .servicosAdicionais(new ArrayList<>(adicionais))
                .build();
    }

    private OrdemServico servicoComPecaInsumo(Peca peca, int qtdPeca, Insumo insumo, int qtdInsumo, boolean aprovado) {
        return OrdemServico.builder()
                .servico(Servico.builder().nome("Revisão de freios").build())
                .aprovado(aprovado)
                .pecas(new ArrayList<>(List.of(
                        OrdemPeca.builder().peca(peca).quantidade(qtdPeca).precoTotal(BigDecimal.TEN).build()
                )))
                .insumos(new ArrayList<>(List.of(
                        OrdemInsumo.builder().insumo(insumo).quantidade(qtdInsumo).precoTotal(BigDecimal.TEN).build()
                )))
                .build();
    }

    private OrdemServico servicoSoPecas(Peca peca, int quantidade, boolean aprovado) {
        return OrdemServico.builder()
                .servico(Servico.builder().nome("Balanceamento").build())
                .aprovado(aprovado)
                .pecas(new ArrayList<>(List.of(
                        OrdemPeca.builder().peca(peca).quantidade(quantidade).precoTotal(BigDecimal.TEN).build()
                )))
                .insumos(new ArrayList<>())
                .build();
    }

    private Peca pecaComEstoque(int estoque, int estoqueReservado) {
        return Peca.builder()
                .id((long) (Math.random() * 1000))
                .nome("Peca")
                .preco(new BigDecimal("10.00"))
                .estoque(estoque)
                .estoqueReservado(estoqueReservado)
                .build();
    }

    private Insumo insumoComEstoque(int estoque, int estoqueReservado) {
        return Insumo.builder()
                .id((long) (Math.random() * 1000))
                .nome("Insumo")
                .preco(new BigDecimal("5.00"))
                .estoque(estoque)
                .estoqueReservado(estoqueReservado)
                .aplicacoes(new ArrayList<>())
                .build();
    }
}