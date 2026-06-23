package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.*;
import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.UpdateInsumoGateway;
import com.grupo52.tech_challenge.gateway.UpdateOSGateway;
import com.grupo52.tech_challenge.gateway.UpdatePecaGateway;
import com.grupo52.tech_challenge.service.impl.FinalizeOSServiceImpl;
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
public class FinalizeOSServiceImplTest {

    @Mock
    private FindOSGateway findOSGateway;

    @Mock
    private UpdateOSStatusService updateOSStatusService;

    @Mock
    private UpdateOSGateway updateOSGateway;

    @Mock
    private UpdatePecaGateway updatePecaGateway;

    @Mock
    private UpdateInsumoGateway updateInsumoGateway;

    @InjectMocks
    private FinalizeOSServiceImpl finalizeOSService;

    @Test
    public void executeSuccess() throws GatewayException, ValidationException, ServiceException {
        Long osId = 1L;
        OrdemDeServico os = osEmExecucao(List.of(), List.of(), List.of());

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> {
            OrdemDeServico osArg = invocation.getArgument(0);
            osArg.setStatus(invocation.getArgument(1));
            return null;
        }).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenReturn(os);

        OrdemDeServico result = finalizeOSService.execute(osId);

        assertEquals(StatusOS.FINALIZADA, result.getStatus());
        verify(findOSGateway, times(1)).execute(osId);
        verify(updateOSStatusService, times(1)).execute(os, StatusOS.FINALIZADA);
        verify(updateOSGateway, times(1)).execute(os);
        verifyNoInteractions(updatePecaGateway, updateInsumoGateway);
    }

    @Test
    public void consumesPecaAndInsumoForApprovedServicos() throws GatewayException, ValidationException, ServiceException {
        Peca peca = pecaComEstoque(10, 4);
        Insumo insumo = insumoComEstoque(5, 1);
        ServicoOS servicoAprovado = servicoComPecaInsumo(peca, 4, insumo, 1, true);

        OrdemDeServico os = osEmExecucao(List.of(servicoAprovado), List.of(), List.of());

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doNothing().when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));
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
    public void skipsNonApprovedServicos() throws GatewayException, ValidationException, ServiceException {
        Peca peca = pecaComEstoque(10, 4);
        ServicoOS servicoRecusado = servicoSoPecas(peca, 4, false);

        OrdemDeServico os = osEmExecucao(List.of(servicoRecusado), List.of(), List.of());

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doNothing().when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        finalizeOSService.execute(1L);

        assertEquals(4, peca.getEstoqueReservado());
        assertEquals(10, peca.getEstoque());
        verifyNoInteractions(updatePecaGateway, updateInsumoGateway);
    }

    @Test
    public void consumesAcrossAllThreeLists() throws GatewayException, ValidationException, ServiceException {
        Peca pecaDesejado = pecaComEstoque(10, 2);
        Peca pecaNecessario = pecaComEstoque(8, 1);
        Peca pecaAdicional = pecaComEstoque(5, 1);

        OrdemDeServico os = osEmExecucao(
                List.of(servicoSoPecas(pecaDesejado, 2, true)),
                List.of(servicoSoPecas(pecaNecessario, 1, true)),
                List.of(servicoSoPecas(pecaAdicional, 1, true))
        );

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doNothing().when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));
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
        doThrow(GatewayException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> finalizeOSService.execute(1L));
    }

    @Test
    public void onValidationException() throws GatewayException, ValidationException, ServiceException {
        OrdemDeServico os = osEmExecucao(List.of(), List.of(), List.of());

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doThrow(ValidationException.class).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));

        assertThrows(ValidationException.class, () -> finalizeOSService.execute(1L));

        verifyNoInteractions(updatePecaGateway, updateInsumoGateway, updateOSGateway);
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(ServiceException.class, () -> finalizeOSService.execute(1L));
    }

    private OrdemDeServico osEmExecucao(List<ServicoOS> desejados, List<ServicoOS> necessarios, List<ServicoOS> adicionais) {
        return OrdemDeServico.builder()
                .id(1L)
                .status(StatusOS.EM_EXECUCAO)
                .cliente(OrdemDeServicoFixture.clienteJoaoSilva())
                .veiculo(OrdemDeServicoFixture.veiculoCorollaABC1D23())
                .servicosDesejados(new ArrayList<>(desejados))
                .servicosNecessarios(new ArrayList<>(necessarios))
                .servicosAdicionais(new ArrayList<>(adicionais))
                .build();
    }

    private ServicoOS servicoComPecaInsumo(Peca peca, int qtdPeca, Insumo insumo, int qtdInsumo, boolean aprovado) {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Revisão de freios").build())
                .aprovado(aprovado)
                .pecas(new ArrayList<>(List.of(
                        PecaOS.builder().peca(peca).quantidade(qtdPeca).precoTotal(BigDecimal.TEN).build()
                )))
                .insumos(new ArrayList<>(List.of(
                        InsumoOS.builder().insumo(insumo).quantidade(qtdInsumo).precoTotal(BigDecimal.TEN).build()
                )))
                .build();
    }

    private ServicoOS servicoSoPecas(Peca peca, int quantidade, boolean aprovado) {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Balanceamento").build())
                .aprovado(aprovado)
                .pecas(new ArrayList<>(List.of(
                        PecaOS.builder().peca(peca).quantidade(quantidade).precoTotal(BigDecimal.TEN).build()
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