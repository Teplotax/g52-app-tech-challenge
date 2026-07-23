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
import com.grupo52.tech_challenge.usecase.impl.CancelOrdemUseCaseImpl;
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
class CancelOrdemUseCaseImplTest {

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
    private CancelOrdemUseCaseImpl cancelOSService;

    @Test
    public void cancelFromRecebidaReleasesAll() throws GatewayException, ValidationException, UseCaseException {
        Peca peca = pecaComEstoqueReservado(4);
        Insumo insumo = insumoComEstoqueReservado(1);
        Ordem os = osComStatus(Status.RECEBIDA,
                List.of(servicoComProdutos(peca, insumo)),
                List.of(),
                List.of());

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> { os.setStatus(invocation.getArgument(1)); return null; })
                .when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updateInsumoGateway.execute(any(Insumo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = cancelOSService.execute(1L);

        assertEquals(Status.CANCELADA, result.getStatus());
        assertEquals(0, peca.getEstoqueReservado());
        assertEquals(0, insumo.getEstoqueReservado());
        verify(updatePecaGateway, times(1)).execute(peca);
        verify(updateInsumoGateway, times(1)).execute(insumo);
    }

    @Test
    public void cancelFromEmDiagnosticoReleasesAll() throws GatewayException, ValidationException, UseCaseException {
        Peca peca = pecaComEstoqueReservado(2);
        Ordem os = osComStatus(Status.EM_DIAGNOSTICO,
                List.of(servicoSoPecas(peca)),
                List.of(),
                List.of());

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> { os.setStatus(invocation.getArgument(1)); return null; })
                .when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = cancelOSService.execute(1L);

        assertEquals(Status.CANCELADA, result.getStatus());
        assertEquals(0, peca.getEstoqueReservado());
        verify(updatePecaGateway, times(1)).execute(peca);
        verifyNoInteractions(updateInsumoGateway);
    }

    @Test
    public void cancelFromAguardandoAprovacaoReleasesAll() throws GatewayException, ValidationException, UseCaseException {
        Peca peca = pecaComEstoqueReservado(3);
        Ordem os = osComStatus(Status.AGUARDANDO_APROVACAO,
                List.of(),
                List.of(servicoSoPecas(peca)),
                List.of());

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> { os.setStatus(invocation.getArgument(1)); return null; })
                .when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = cancelOSService.execute(1L);

        assertEquals(Status.CANCELADA, result.getStatus());
        assertEquals(0, peca.getEstoqueReservado());
        verify(updatePecaGateway, times(1)).execute(peca);
    }

    @Test
    public void cancelFromAprovadaReleasesOnlyApproved() throws GatewayException, ValidationException, UseCaseException {
        Peca pecaAprovada = pecaComEstoqueReservado(4);
        Peca pecaRecusada = pecaComEstoqueReservado(2);

        OrdemServico servicoAprovado = servicoComAprovado(pecaAprovada, true);
        OrdemServico servicoRecusado = servicoComAprovado(pecaRecusada, false);

        Ordem os = osComStatus(Status.APROVADA,
                List.of(servicoAprovado),
                List.of(servicoRecusado),
                List.of());

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> { os.setStatus(invocation.getArgument(1)); return null; })
                .when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = cancelOSService.execute(1L);

        assertEquals(Status.CANCELADA, result.getStatus());
        assertEquals(0, pecaAprovada.getEstoqueReservado());
        assertEquals(2, pecaRecusada.getEstoqueReservado());
        verify(updatePecaGateway, times(1)).execute(pecaAprovada);
        verify(updatePecaGateway, never()).execute(pecaRecusada);
    }

    @Test
    public void cancelFromEmExecucaoReleasesOnlyApproved() throws GatewayException, ValidationException, UseCaseException {
        Peca peca = pecaComEstoqueReservado(2);
        OrdemServico servicoAprovado = servicoComAprovado(peca, true);

        Ordem os = osComStatus(Status.EM_EXECUCAO,
                List.of(servicoAprovado),
                List.of(),
                List.of());

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> { os.setStatus(invocation.getArgument(1)); return null; })
                .when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = cancelOSService.execute(1L);

        assertEquals(Status.CANCELADA, result.getStatus());
        assertEquals(0, peca.getEstoqueReservado());
        verify(updatePecaGateway, times(1)).execute(peca);
    }

    @Test
    public void cancelFromAguardandoAquisicaoReleasesOnlyApproved() throws GatewayException, ValidationException, UseCaseException {
        Peca pecaAprovadaReservada = pecaComEstoqueReservado(2);
        Peca pecaNaoReservada = pecaComEstoqueReservado(0);

        OrdemServico servicoAprovado = servicoComAprovado(pecaAprovadaReservada, true);
        OrdemServico servicoAprovadoSemEstoque = OrdemServico.builder()
                .servico(Servico.builder().nome("Troca de disco").build())
                .aprovado(true)
                .pecas(new ArrayList<>(List.of(
                        OrdemPeca.builder().peca(pecaNaoReservada).quantidade(2).precoTotal(BigDecimal.TEN).reservado(false).build()
                )))
                .insumos(new ArrayList<>())
                .build();

        Ordem os = osComStatus(Status.AGUARDANDO_AQUISICAO,
                List.of(servicoAprovado, servicoAprovadoSemEstoque),
                List.of(),
                List.of());

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> { os.setStatus(invocation.getArgument(1)); return null; })
                .when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = cancelOSService.execute(1L);

        assertEquals(Status.CANCELADA, result.getStatus());
        assertEquals(0, pecaAprovadaReservada.getEstoqueReservado());
        assertEquals(0, pecaNaoReservada.getEstoqueReservado());
        verify(updatePecaGateway, times(1)).execute(pecaAprovadaReservada);
        verify(updatePecaGateway, never()).execute(pecaNaoReservada);
    }

    @Test
    public void cancelReleasesAcrossAllThreeLists() throws GatewayException, ValidationException, UseCaseException {
        Peca pecaDesejado = pecaComEstoqueReservado(1);
        Peca pecaNecessario = pecaComEstoqueReservado(2);
        Peca pecaAdicional = pecaComEstoqueReservado(1);

        Ordem os = osComStatus(Status.RECEBIDA,
                List.of(servicoSoPecas(pecaDesejado)),
                List.of(servicoSoPecas(pecaNecessario)),
                List.of(servicoSoPecas(pecaAdicional)));

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> { os.setStatus(invocation.getArgument(1)); return null; })
                .when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cancelOSService.execute(1L);

        assertEquals(0, pecaDesejado.getEstoqueReservado());
        assertEquals(0, pecaNecessario.getEstoqueReservado());
        assertEquals(0, pecaAdicional.getEstoqueReservado());
        verify(updatePecaGateway, times(3)).execute(any(Peca.class));
    }

    @Test
    public void cancelSkipsReleaseForProdutosNuncaReservados() throws GatewayException, ValidationException, UseCaseException {
        Peca pecaNaoReservada = pecaComEstoqueReservado(0);
        Insumo insumoNaoReservado = insumoComEstoqueReservado(0);

        OrdemServico servico = OrdemServico.builder()
                .servico(Servico.builder().nome("Revisão de freios").build())
                .aprovado(null)
                .pecas(new ArrayList<>(List.of(
                        OrdemPeca.builder().peca(pecaNaoReservada).quantidade(2).precoTotal(BigDecimal.TEN).reservado(false).build()
                )))
                .insumos(new ArrayList<>(List.of(
                        OrdemInsumo.builder().insumo(insumoNaoReservado).quantidade(1).precoTotal(BigDecimal.TEN).reservado(false).build()
                )))
                .build();

        Ordem os = osComStatus(Status.RECEBIDA, List.of(servico), List.of(), List.of());

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> { os.setStatus(invocation.getArgument(1)); return null; })
                .when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = cancelOSService.execute(1L);

        assertEquals(Status.CANCELADA, result.getStatus());
        assertEquals(0, pecaNaoReservada.getEstoqueReservado());
        assertEquals(0, insumoNaoReservado.getEstoqueReservado());
        verifyNoInteractions(updatePecaGateway, updateInsumoGateway);
    }

    @Test
    public void onGatewayException() throws GatewayException {
        doThrow(GatewayException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> cancelOSService.execute(1L));
    }

    @Test
    public void onValidationException() throws GatewayException, ValidationException, UseCaseException {
        Ordem os = OrdemDeServicoFixture.recebida(1L);

        when(findOrdemGateway.execute(any(Long.class))).thenReturn(os);
        doThrow(ValidationException.class).when(updateOrdemStatusUseCase).execute(any(Ordem.class), any(Status.class));

        assertThrows(ValidationException.class, () -> cancelOSService.execute(1L));

        verifyNoInteractions(updatePecaGateway, updateInsumoGateway, updateOrdemGateway);
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOrdemGateway).execute(any(Long.class));

        assertThrows(UseCaseException.class, () -> cancelOSService.execute(1L));
    }

    private Ordem osComStatus(Status status,
                              List<OrdemServico> desejados,
                              List<OrdemServico> necessarios,
                              List<OrdemServico> adicionais) {
        return Ordem.builder()
                .id(1L)
                .status(status)
                .cliente(OrdemDeServicoFixture.clienteJoaoSilva())
                .veiculo(OrdemDeServicoFixture.veiculoCorollaABC1D23())
                .servicosDesejados(new ArrayList<>(desejados))
                .servicosNecessarios(new ArrayList<>(necessarios))
                .servicosAdicionais(new ArrayList<>(adicionais))
                .build();
    }

    private OrdemServico servicoComProdutos(Peca peca, Insumo insumo) {
        return OrdemServico.builder()
                .servico(Servico.builder().nome("Revisão de freios").build())
                .aprovado(null)
                .pecas(new ArrayList<>(List.of(
                        OrdemPeca.builder().peca(peca).quantidade(peca.getEstoqueReservado()).precoTotal(BigDecimal.TEN).reservado(true).build()
                )))
                .insumos(new ArrayList<>(List.of(
                        OrdemInsumo.builder().insumo(insumo).quantidade(insumo.getEstoqueReservado()).precoTotal(BigDecimal.TEN).reservado(true).build()
                )))
                .build();
    }

    private OrdemServico servicoSoPecas(Peca peca) {
        return OrdemServico.builder()
                .servico(Servico.builder().nome("Balanceamento").build())
                .aprovado(null)
                .pecas(new ArrayList<>(List.of(
                        OrdemPeca.builder().peca(peca).quantidade(peca.getEstoqueReservado()).precoTotal(BigDecimal.TEN).reservado(true).build()
                )))
                .insumos(new ArrayList<>())
                .build();
    }

    private OrdemServico servicoComAprovado(Peca peca, boolean aprovado) {
        return OrdemServico.builder()
                .servico(Servico.builder().nome("Serviço").build())
                .aprovado(aprovado)
                .pecas(new ArrayList<>(List.of(
                        OrdemPeca.builder().peca(peca).quantidade(peca.getEstoqueReservado()).precoTotal(BigDecimal.TEN).reservado(true).build()
                )))
                .insumos(new ArrayList<>())
                .build();
    }

    private Peca pecaComEstoqueReservado(int quantidade) {
        return Peca.builder()
                .id((long) (Math.random() * 1000))
                .nome("Peca")
                .preco(new BigDecimal("10.00"))
                .estoqueReservado(quantidade)
                .build();
    }

    private Insumo insumoComEstoqueReservado(int quantidade) {
        return Insumo.builder()
                .id((long) (Math.random() * 1000))
                .nome("Insumo")
                .preco(new BigDecimal("5.00"))
                .estoqueReservado(quantidade)
                .aplicacoes(new ArrayList<>())
                .build();
    }
}