package com.grupo52.tech_challenge.usecase;

import com.grupo52.tech_challenge.domain.*;
import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.gateway.*;
import com.grupo52.tech_challenge.usecase.impl.CalculateOrdemPriceUseCaseImpl;
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
class CalculateOrdemPriceUseCaseImplTest {

    @Mock
    private UpdateOrdemGateway updateOrdemGateway;

    @Mock
    private FindServicoGateway findServicoGateway;

    @Mock
    private FindPecaByVeiculoGateway findPecaByVeiculoGateway;

    @Mock
    private FindInsumoByVeiculoGateway findInsumoByVeiculoGateway;

    @Mock
    private UpdatePecaGateway updatePecaGateway;

    @Mock
    private UpdateInsumoGateway updateInsumoGateway;

    @InjectMocks
    private CalculateOrdemPriceUseCaseImpl calculateOSPriceService;

    @Test
    public void calculateServicosDesejadosSuccess() throws GatewayException, UseCaseException {
        Long osId = 1L;
        Long servicoId = 10L;
        Veiculo veiculo = veiculoComModelo();
        OrdemServico ordemServico = OrdemServico.builder().servico(Servico.builder().id(servicoId).build()).build();

        Ordem os = Ordem.builder()
                .id(osId)
                .veiculo(veiculo)
                .servicosDesejados(new ArrayList<>(List.of(ordemServico)))
                .build();

        stubCatalogo(servicoId, veiculo);
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = calculateOSPriceService.calculateServicosDesejados(os);

        assertCalculatedTotals(result.getPrecoServicosDesejados(), result.getPrecoTotal(), ordemServico);
    }

    @Test
    public void calculateServicosNecessariosSuccess() throws GatewayException, UseCaseException {
        Long osId = 1L;
        Long servicoId = 10L;
        Veiculo veiculo = veiculoComModelo();
        OrdemServico ordemServico = OrdemServico.builder().servico(Servico.builder().id(servicoId).build()).build();

        Ordem os = Ordem.builder()
                .id(osId)
                .veiculo(veiculo)
                .servicosNecessarios(new ArrayList<>(List.of(ordemServico)))
                .build();

        stubCatalogo(servicoId, veiculo);
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = calculateOSPriceService.calculateServicosNecessarios(os);

        assertCalculatedTotals(result.getPrecoServicosNecessarios(), result.getPrecoTotal(), ordemServico);
    }

    @Test
    public void calculateServicosAdicionaisSuccess() throws GatewayException, UseCaseException {
        Long osId = 1L;
        Long servicoId = 10L;
        Veiculo veiculo = veiculoComModelo();
        OrdemServico ordemServico = OrdemServico.builder().servico(Servico.builder().id(servicoId).build()).build();

        Ordem os = Ordem.builder()
                .id(osId)
                .veiculo(veiculo)
                .servicosAdicionais(new ArrayList<>(List.of(ordemServico)))
                .build();

        stubCatalogo(servicoId, veiculo);
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = calculateOSPriceService.calculateServicosAdicionais(os);

        assertCalculatedTotals(result.getPrecoServicosAdicionais(), result.getPrecoTotal(), ordemServico);
    }

    @Test
    public void calculateServicosDesejadosPicksFirstPecaWithSufficientStock() throws GatewayException, UseCaseException {
        Long servicoId = 10L;
        Veiculo veiculo = veiculoComModelo();
        OrdemServico ordemServico = OrdemServico.builder().servico(Servico.builder().id(servicoId).build()).build();

        Ordem os = Ordem.builder()
                .id(1L)
                .veiculo(veiculo)
                .servicosDesejados(new ArrayList<>(List.of(ordemServico)))
                .build();

        Servico servico = servicoComPecaEInsumo(servicoId);

        Peca pecaSemEstoque = Peca.builder()
                .id(101L)
                .nome("Pastilha sem estoque")
                .preco(new BigDecimal("10.00"))
                .estoque(1)
                .estoqueReservado(1)
                .build();

        Peca pecaComEstoque = Peca.builder()
                .id(100L)
                .nome("Pastilha de freio dianteira")
                .preco(new BigDecimal("10.00"))
                .estoque(10)
                .estoqueReservado(0)
                .build();

        AplicacaoProduto aplicacao = AplicacaoProduto.builder()
                .modelo(veiculo.getModelo())
                .quantidade(3)
                .build();

        Insumo insumo = Insumo.builder()
                .id(200L)
                .nome("Fluido de freio DOT 4")
                .preco(new BigDecimal("5.00"))
                .estoque(10)
                .estoqueReservado(0)
                .aplicacoes(List.of(aplicacao))
                .build();

        when(findServicoGateway.execute(servicoId)).thenReturn(servico);
        when(findPecaByVeiculoGateway.execute(TipoPeca.PASTILHA_FREIO, veiculo)).thenReturn(List.of(pecaSemEstoque, pecaComEstoque));
        when(findInsumoByVeiculoGateway.execute(TipoInsumo.FLUIDO_FREIO, veiculo)).thenReturn(List.of(insumo));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = calculateOSPriceService.calculateServicosDesejados(os);

        assertEquals(100L, ordemServico.getPecas().getFirst().getPeca().getId());
    }

    @Test
    public void calculateServicosDesejadosThrowsWhenNoPecaHasSufficientStock() throws GatewayException {
        Long servicoId = 10L;
        Veiculo veiculo = veiculoComModelo();
        OrdemServico ordemServico = OrdemServico.builder().servico(Servico.builder().id(servicoId).build()).build();

        Ordem os = Ordem.builder()
                .id(1L)
                .veiculo(veiculo)
                .servicosDesejados(new ArrayList<>(List.of(ordemServico)))
                .build();

        Servico servico = servicoComPecaEInsumo(servicoId);

        Peca pecaSemEstoque = Peca.builder()
                .id(100L)
                .nome("Pastilha de freio")
                .preco(new BigDecimal("10.00"))
                .estoque(1)
                .estoqueReservado(1)
                .build();

        when(findServicoGateway.execute(servicoId)).thenReturn(servico);
        when(findPecaByVeiculoGateway.execute(TipoPeca.PASTILHA_FREIO, veiculo)).thenReturn(List.of(pecaSemEstoque));

        UseCaseException ex = assertThrows(UseCaseException.class, () ->
                calculateOSPriceService.calculateServicosDesejados(os)
        );

        assertEquals(422, ex.getStatus());
    }

    @Test
    public void calculateServicosDesejadosThrowsWhenNoInsumoHasSufficientStock() throws GatewayException {
        Long servicoId = 10L;
        Veiculo veiculo = veiculoComModelo();
        OrdemServico ordemServico = OrdemServico.builder().servico(Servico.builder().id(servicoId).build()).build();

        Ordem os = Ordem.builder()
                .id(1L)
                .veiculo(veiculo)
                .servicosDesejados(new ArrayList<>(List.of(ordemServico)))
                .build();

        Servico servico = servicoComPecaEInsumo(servicoId);

        Peca peca = Peca.builder()
                .id(100L)
                .nome("Pastilha de freio dianteira")
                .preco(new BigDecimal("10.00"))
                .estoque(10)
                .estoqueReservado(0)
                .build();

        AplicacaoProduto aplicacao = AplicacaoProduto.builder()
                .modelo(veiculo.getModelo())
                .quantidade(3)
                .build();

        Insumo insumoSemEstoque = Insumo.builder()
                .id(200L)
                .nome("Fluido de freio DOT 4")
                .preco(new BigDecimal("5.00"))
                .estoque(2)
                .estoqueReservado(2)
                .aplicacoes(List.of(aplicacao))
                .build();

        when(findServicoGateway.execute(servicoId)).thenReturn(servico);
        when(findPecaByVeiculoGateway.execute(TipoPeca.PASTILHA_FREIO, veiculo)).thenReturn(List.of(peca));
        when(findInsumoByVeiculoGateway.execute(TipoInsumo.FLUIDO_FREIO, veiculo)).thenReturn(List.of(insumoSemEstoque));

        UseCaseException ex = assertThrows(UseCaseException.class, () ->
                calculateOSPriceService.calculateServicosDesejados(os)
        );

        assertEquals(422, ex.getStatus());
    }

    @Test
    public void calculateApprovedPriceSuccess() throws GatewayException, UseCaseException {
        Peca peca = Peca.builder()
                .id(100L)
                .nome("Pastilha de freio dianteira")
                .preco(new BigDecimal("35.00"))
                .estoqueReservado(0)
                .build();

        Insumo insumo = Insumo.builder()
                .id(200L)
                .nome("Fluido de freio DOT 4")
                .preco(new BigDecimal("25.00"))
                .estoqueReservado(0)
                .aplicacoes(new ArrayList<>())
                .build();

        OrdemServico servicoAprovado = OrdemServico.builder()
                .servico(Servico.builder().nome("Revisão de freios").horasTecnicas(new BigDecimal("2.0")).build())
                .precoTotal(new BigDecimal("320.00"))
                .pecas(new ArrayList<>(List.of(OrdemPeca.builder().peca(peca).quantidade(2).precoTotal(new BigDecimal("70.00")).build())))
                .insumos(new ArrayList<>(List.of(OrdemInsumo.builder().insumo(insumo).quantidade(1).precoTotal(new BigDecimal("25.00")).build())))
                .build();
        servicoAprovado.setAprovado(true);

        Ordem os = Ordem.builder()
                .id(1L)
                .veiculo(veiculoComModelo())
                .precoServicosDesejados(new BigDecimal("320.00"))
                .servicosDesejados(new ArrayList<>(List.of(servicoAprovado)))
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();

        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = calculateOSPriceService.calculateApprovedPrice(os);

        assertEquals(0, new BigDecimal("320.00").compareTo(result.getPrecoServicosDesejados()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPrecoServicosNecessarios()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPrecoServicosAdicionais()));
        assertEquals(0, new BigDecimal("320.00").compareTo(result.getPrecoTotal()));
        assertEquals(0, peca.getEstoqueReservado());
        verifyNoInteractions(updatePecaGateway);
        verifyNoInteractions(updateInsumoGateway);
        verify(updateOrdemGateway, times(1)).execute(any(Ordem.class));
    }

    @Test
    public void calculateApprovedPriceReleasesRefusedServicos() throws GatewayException, UseCaseException {
        Peca peca = Peca.builder()
                .id(100L)
                .nome("Pastilha de freio dianteira")
                .preco(new BigDecimal("35.00"))
                .estoqueReservado(4)
                .build();

        OrdemServico servicoRecusado = OrdemServico.builder()
                .servico(Servico.builder().nome("Revisão de freios").horasTecnicas(new BigDecimal("2.0")).build())
                .precoTotal(new BigDecimal("320.00"))
                .pecas(new ArrayList<>(List.of(OrdemPeca.builder().peca(peca).quantidade(4).precoTotal(new BigDecimal("140.00")).build())))
                .insumos(new ArrayList<>())
                .aprovado(false)
                .build();

        Ordem os = Ordem.builder()
                .id(1L)
                .veiculo(veiculoComModelo())
                .servicosDesejados(new ArrayList<>(List.of(servicoRecusado)))
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();

        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = calculateOSPriceService.calculateApprovedPrice(os);

        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPrecoServicosDesejados()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPrecoTotal()));
        assertEquals(0, peca.getEstoqueReservado());
        verify(updatePecaGateway, times(1)).execute(peca);
        verifyNoInteractions(updateInsumoGateway);
        verify(updateOrdemGateway, times(1)).execute(any(Ordem.class));
    }

    @Test
    public void calculateApprovedPriceSkipsUnapprovedServicos() throws GatewayException, UseCaseException {
        OrdemServico servicoNaoAprovado = OrdemServico.builder()
                .servico(Servico.builder().nome("Balanceamento de rodas").horasTecnicas(new BigDecimal("1.0")).build())
                .precoTotal(new BigDecimal("165.00"))
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>())
                .aprovado(false)
                .build();

        Ordem os = Ordem.builder()
                .id(1L)
                .veiculo(veiculoComModelo())
                .servicosDesejados(new ArrayList<>(List.of(servicoNaoAprovado)))
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();

        when(updateOrdemGateway.execute(any(Ordem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = calculateOSPriceService.calculateApprovedPrice(os);

        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPrecoServicosDesejados()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPrecoTotal()));
        verifyNoInteractions(updatePecaGateway);
        verifyNoInteractions(updateInsumoGateway);
    }

    @Test
    public void onGatewayException() throws GatewayException {
        Long osId = 1L;
        Long servicoId = 10L;
        Veiculo veiculo = veiculoComModelo();
        OrdemServico ordemServico = OrdemServico.builder().servico(Servico.builder().id(servicoId).build()).build();

        Ordem os = Ordem.builder()
                .id(osId)
                .veiculo(veiculo)
                .servicosDesejados(new ArrayList<>(List.of(ordemServico)))
                .build();

        doThrow(GatewayException.class).when(findServicoGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> {
            calculateOSPriceService.calculateServicosDesejados(os);
        });
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        Long osId = 1L;
        Long servicoId = 10L;
        Veiculo veiculo = veiculoComModelo();
        OrdemServico ordemServico = OrdemServico.builder().servico(Servico.builder().id(servicoId).build()).build();

        Ordem os = Ordem.builder()
                .id(osId)
                .veiculo(veiculo)
                .servicosDesejados(new ArrayList<>(List.of(ordemServico)))
                .build();

        doThrow(RuntimeException.class).when(findServicoGateway).execute(any(Long.class));

        assertThrows(UseCaseException.class, () -> {
            calculateOSPriceService.calculateServicosDesejados(os);
        });
    }

    private void stubCatalogo(Long servicoId, Veiculo veiculo) throws GatewayException {
        Servico servico = servicoComPecaEInsumo(servicoId);

        Peca peca = Peca.builder()
                .id(100L)
                .nome("Pastilha de freio dianteira")
                .preco(new BigDecimal("10.00"))
                .estoque(10)
                .estoqueReservado(0)
                .build();

        AplicacaoProduto aplicacao = AplicacaoProduto.builder()
                .modelo(veiculo.getModelo())
                .quantidade(3)
                .build();

        Insumo insumo = Insumo.builder()
                .id(200L)
                .nome("Fluido de freio DOT 4")
                .preco(new BigDecimal("5.00"))
                .estoque(10)
                .estoqueReservado(0)
                .aplicacoes(List.of(aplicacao))
                .build();

        when(findServicoGateway.execute(servicoId)).thenReturn(servico);
        when(findPecaByVeiculoGateway.execute(TipoPeca.PASTILHA_FREIO, veiculo)).thenReturn(List.of(peca));
        when(findInsumoByVeiculoGateway.execute(TipoInsumo.FLUIDO_FREIO, veiculo)).thenReturn(List.of(insumo));
    }

    private Servico servicoComPecaEInsumo(Long servicoId) {
        return Servico.builder()
                .id(servicoId)
                .nome("Troca de pastilhas de freio")
                .horasTecnicas(new BigDecimal("1.0"))
                .pecas(List.of(Servico.ServicoTipoPeca.builder()
                        .tipoPeca(TipoPeca.PASTILHA_FREIO)
                        .quantidade(2)
                        .build()))
                .insumos(List.of(TipoInsumo.FLUIDO_FREIO))
                .build();
    }

    private Veiculo veiculoComModelo() {
        Modelo modelo = Modelo.builder().id(1L).nome("Corolla").build();
        return Veiculo.builder().id(1L).placa("ABC1D23").modelo(modelo).build();
    }

    private void assertCalculatedTotals(BigDecimal precoServicoCategoria, BigDecimal precoTotal, OrdemServico ordemServico) {
        assertEquals(0, new BigDecimal("70.00").compareTo(ordemServico.getPrecoHorasTecnicas()));
        assertEquals(0, new BigDecimal("105.00").compareTo(ordemServico.getPrecoTotal()));
        assertEquals(1, ordemServico.getPecas().size());
        assertEquals(0, new BigDecimal("20.00").compareTo(ordemServico.getPecas().getFirst().getPrecoTotal()));
        assertEquals(1, ordemServico.getInsumos().size());
        assertEquals(0, new BigDecimal("15.00").compareTo(ordemServico.getInsumos().getFirst().getPrecoTotal()));
        assertEquals(0, new BigDecimal("105.00").compareTo(precoServicoCategoria));
        assertEquals(0, new BigDecimal("105.00").compareTo(precoTotal));
    }
}