package com.grupo52.tech_challenge.service;

import com.grupo52.tech_challenge.domain.*;
import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.gateway.*;
import com.grupo52.tech_challenge.service.impl.CalculateOSPriceServiceImpl;
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
public class CalculateOSPriceServiceImplTest {

    @Mock
    private UpdateOSGateway updateOSGateway;

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
    private CalculateOSPriceServiceImpl calculateOSPriceService;

    @Test
    public void calculateServicosDesejadosSuccess() throws GatewayException, ServiceException {
        Long osId = 1L;
        Long servicoId = 10L;
        Veiculo veiculo = veiculoComModelo();
        ServicoOS servicoOS = ServicoOS.builder().servico(Servico.builder().id(servicoId).build()).build();

        OrdemDeServico os = OrdemDeServico.builder()
                .id(osId)
                .veiculo(veiculo)
                .servicosDesejados(new ArrayList<>(List.of(servicoOS)))
                .build();

        stubCatalogo(servicoId, veiculo);
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemDeServico result = calculateOSPriceService.calculateServicosDesejados(os);

        assertCalculatedTotals(result.getPrecoServicosDesejados(), result.getPrecoTotal(), servicoOS);
    }

    @Test
    public void calculateServicosNecessariosSuccess() throws GatewayException, ServiceException {
        Long osId = 1L;
        Long servicoId = 10L;
        Veiculo veiculo = veiculoComModelo();
        ServicoOS servicoOS = ServicoOS.builder().servico(Servico.builder().id(servicoId).build()).build();

        OrdemDeServico os = OrdemDeServico.builder()
                .id(osId)
                .veiculo(veiculo)
                .servicosNecessarios(new ArrayList<>(List.of(servicoOS)))
                .build();

        stubCatalogo(servicoId, veiculo);
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemDeServico result = calculateOSPriceService.calculateServicosNecessarios(os);

        assertCalculatedTotals(result.getPrecoServicosNecessarios(), result.getPrecoTotal(), servicoOS);
    }

    @Test
    public void calculateServicosAdicionaisSuccess() throws GatewayException, ServiceException {
        Long osId = 1L;
        Long servicoId = 10L;
        Veiculo veiculo = veiculoComModelo();
        ServicoOS servicoOS = ServicoOS.builder().servico(Servico.builder().id(servicoId).build()).build();

        OrdemDeServico os = OrdemDeServico.builder()
                .id(osId)
                .veiculo(veiculo)
                .servicosAdicionais(new ArrayList<>(List.of(servicoOS)))
                .build();

        stubCatalogo(servicoId, veiculo);
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemDeServico result = calculateOSPriceService.calculateServicosAdicionais(os);

        assertCalculatedTotals(result.getPrecoServicosAdicionais(), result.getPrecoTotal(), servicoOS);
    }

    @Test
    public void calculateApprovedPriceSuccess() throws GatewayException, ServiceException {
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

        ServicoOS servicoAprovado = ServicoOS.builder()
                .servico(Servico.builder().nome("Revisão de freios").horasTecnicas(new BigDecimal("2.0")).build())
                .precoTotal(new BigDecimal("320.00"))
                .pecas(new ArrayList<>(List.of(PecaOS.builder().peca(peca).quantidade(2).precoTotal(new BigDecimal("70.00")).build())))
                .insumos(new ArrayList<>(List.of(InsumoOS.builder().insumo(insumo).quantidade(1).precoTotal(new BigDecimal("25.00")).build())))
                .build();
        servicoAprovado.setAprovado(true);

        OrdemDeServico os = OrdemDeServico.builder()
                .id(1L)
                .veiculo(veiculoComModelo())
                .precoServicosDesejados(new BigDecimal("320.00"))
                .servicosDesejados(new ArrayList<>(List.of(servicoAprovado)))
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();

        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemDeServico result = calculateOSPriceService.calculateApprovedPrice(os);

        assertEquals(0, new BigDecimal("320.00").compareTo(result.getPrecoServicosDesejados()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPrecoServicosNecessarios()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPrecoServicosAdicionais()));
        assertEquals(0, new BigDecimal("320.00").compareTo(result.getPrecoTotal()));
        assertEquals(0, peca.getEstoqueReservado());
        verifyNoInteractions(updatePecaGateway);
        verifyNoInteractions(updateInsumoGateway);
        verify(updateOSGateway, times(1)).execute(any(OrdemDeServico.class));
    }

    @Test
    public void calculateApprovedPriceReleasesRefusedServicos() throws GatewayException, ServiceException {
        Peca peca = Peca.builder()
                .id(100L)
                .nome("Pastilha de freio dianteira")
                .preco(new BigDecimal("35.00"))
                .estoqueReservado(4)
                .build();

        ServicoOS servicoRecusado = ServicoOS.builder()
                .servico(Servico.builder().nome("Revisão de freios").horasTecnicas(new BigDecimal("2.0")).build())
                .precoTotal(new BigDecimal("320.00"))
                .pecas(new ArrayList<>(List.of(PecaOS.builder().peca(peca).quantidade(4).precoTotal(new BigDecimal("140.00")).build())))
                .insumos(new ArrayList<>())
                .aprovado(false)
                .build();

        OrdemDeServico os = OrdemDeServico.builder()
                .id(1L)
                .veiculo(veiculoComModelo())
                .servicosDesejados(new ArrayList<>(List.of(servicoRecusado)))
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();

        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemDeServico result = calculateOSPriceService.calculateApprovedPrice(os);

        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPrecoServicosDesejados()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPrecoTotal()));
        assertEquals(0, peca.getEstoqueReservado());
        verify(updatePecaGateway, times(1)).execute(peca);
        verifyNoInteractions(updateInsumoGateway);
        verify(updateOSGateway, times(1)).execute(any(OrdemDeServico.class));
    }

    @Test
    public void calculateApprovedPriceSkipsUnapprovedServicos() throws GatewayException, ServiceException {
        ServicoOS servicoNaoAprovado = ServicoOS.builder()
                .servico(Servico.builder().nome("Balanceamento de rodas").horasTecnicas(new BigDecimal("1.0")).build())
                .precoTotal(new BigDecimal("165.00"))
                .pecas(new ArrayList<>())
                .insumos(new ArrayList<>())
                .aprovado(false)
                .build();

        OrdemDeServico os = OrdemDeServico.builder()
                .id(1L)
                .veiculo(veiculoComModelo())
                .servicosDesejados(new ArrayList<>(List.of(servicoNaoAprovado)))
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();

        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemDeServico result = calculateOSPriceService.calculateApprovedPrice(os);

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
        ServicoOS servicoOS = ServicoOS.builder().servico(Servico.builder().id(servicoId).build()).build();

        OrdemDeServico os = OrdemDeServico.builder()
                .id(osId)
                .veiculo(veiculo)
                .servicosDesejados(new ArrayList<>(List.of(servicoOS)))
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
        ServicoOS servicoOS = ServicoOS.builder().servico(Servico.builder().id(servicoId).build()).build();

        OrdemDeServico os = OrdemDeServico.builder()
                .id(osId)
                .veiculo(veiculo)
                .servicosDesejados(new ArrayList<>(List.of(servicoOS)))
                .build();

        doThrow(RuntimeException.class).when(findServicoGateway).execute(any(Long.class));

        assertThrows(ServiceException.class, () -> {
            calculateOSPriceService.calculateServicosDesejados(os);
        });
    }

    private void stubCatalogo(Long servicoId, Veiculo veiculo) throws GatewayException {
        Servico servico = Servico.builder()
                .id(servicoId)
                .nome("Troca de pastilhas de freio")
                .horasTecnicas(new BigDecimal("1.0"))
                .pecas(List.of(Servico.ServicoTipoPeca.builder()
                        .tipoPeca(TipoPeca.PASTILHA_FREIO)
                        .quantidade(2)
                        .build()))
                .insumos(List.of(TipoInsumo.FLUIDO_FREIO))
                .build();

        Peca peca = Peca.builder()
                .id(100L)
                .nome("Pastilha de freio dianteira")
                .preco(new BigDecimal("10.00"))
                .build();

        AplicacaoProduto aplicacao = AplicacaoProduto.builder()
                .modelo(veiculo.getModelo())
                .quantidade(3)
                .build();

        Insumo insumo = Insumo.builder()
                .id(200L)
                .nome("Fluido de freio DOT 4")
                .preco(new BigDecimal("5.00"))
                .aplicacoes(List.of(aplicacao))
                .build();

        when(findServicoGateway.execute(servicoId)).thenReturn(servico);
        when(findPecaByVeiculoGateway.execute(TipoPeca.PASTILHA_FREIO, veiculo)).thenReturn(List.of(peca));
        when(findInsumoByVeiculoGateway.execute(TipoInsumo.FLUIDO_FREIO, veiculo)).thenReturn(List.of(insumo));
    }

    private Veiculo veiculoComModelo() {
        Modelo modelo = Modelo.builder().id(1L).nome("Corolla").build();
        return Veiculo.builder().id(1L).placa("ABC1D23").modelo(modelo).build();
    }

    private void assertCalculatedTotals(BigDecimal precoServicoCategoria, BigDecimal precoTotal, ServicoOS servicoOS) {
        assertEquals(0, new BigDecimal("70.00").compareTo(servicoOS.getPrecoHorasTecnicas()));
        assertEquals(0, new BigDecimal("105.00").compareTo(servicoOS.getPrecoTotal()));
        assertEquals(1, servicoOS.getPecas().size());
        assertEquals(0, new BigDecimal("20.00").compareTo(servicoOS.getPecas().getFirst().getPrecoTotal()));
        assertEquals(1, servicoOS.getInsumos().size());
        assertEquals(0, new BigDecimal("15.00").compareTo(servicoOS.getInsumos().getFirst().getPrecoTotal()));
        assertEquals(0, new BigDecimal("105.00").compareTo(precoServicoCategoria));
        assertEquals(0, new BigDecimal("105.00").compareTo(precoTotal));
    }
}