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
import com.grupo52.tech_challenge.service.impl.CancelOSServiceImpl;
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
public class CancelOSServiceImplTest {

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
    private CancelOSServiceImpl cancelOSService;

    @Test
    public void cancelFromRecebidaReleasesAll() throws GatewayException, ValidationException, ServiceException {
        Peca peca = pecaComEstoqueReservado(4);
        Insumo insumo = insumoComEstoqueReservado(1);
        OrdemDeServico os = osComStatus(StatusOS.RECEBIDA,
                List.of(servicoComProdutos(peca, insumo)),
                List.of(),
                List.of());

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> { os.setStatus(invocation.getArgument(1)); return null; })
                .when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updateInsumoGateway.execute(any(Insumo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemDeServico result = cancelOSService.execute(1L);

        assertEquals(StatusOS.CANCELADA, result.getStatus());
        assertEquals(0, peca.getEstoqueReservado());
        assertEquals(0, insumo.getEstoqueReservado());
        verify(updatePecaGateway, times(1)).execute(peca);
        verify(updateInsumoGateway, times(1)).execute(insumo);
    }

    @Test
    public void cancelFromEmDiagnosticoReleasesAll() throws GatewayException, ValidationException, ServiceException {
        Peca peca = pecaComEstoqueReservado(2);
        OrdemDeServico os = osComStatus(StatusOS.EM_DIAGNOSTICO,
                List.of(servicoSoPecas(peca)),
                List.of(),
                List.of());

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> { os.setStatus(invocation.getArgument(1)); return null; })
                .when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemDeServico result = cancelOSService.execute(1L);

        assertEquals(StatusOS.CANCELADA, result.getStatus());
        assertEquals(0, peca.getEstoqueReservado());
        verify(updatePecaGateway, times(1)).execute(peca);
        verifyNoInteractions(updateInsumoGateway);
    }

    @Test
    public void cancelFromAguardandoAprovacaoReleasesAll() throws GatewayException, ValidationException, ServiceException {
        Peca peca = pecaComEstoqueReservado(3);
        OrdemDeServico os = osComStatus(StatusOS.AGUARDANDO_APROVACAO,
                List.of(),
                List.of(servicoSoPecas(peca)),
                List.of());

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> { os.setStatus(invocation.getArgument(1)); return null; })
                .when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemDeServico result = cancelOSService.execute(1L);

        assertEquals(StatusOS.CANCELADA, result.getStatus());
        assertEquals(0, peca.getEstoqueReservado());
        verify(updatePecaGateway, times(1)).execute(peca);
    }

    @Test
    public void cancelFromAprovadaReleasesOnlyApproved() throws GatewayException, ValidationException, ServiceException {
        Peca pecaAprovada = pecaComEstoqueReservado(4);
        Peca pecaRecusada = pecaComEstoqueReservado(2);

        ServicoOS servicoAprovado = servicoComAprovado(pecaAprovada, true);
        ServicoOS servicoRecusado = servicoComAprovado(pecaRecusada, false);

        OrdemDeServico os = osComStatus(StatusOS.APROVADA,
                List.of(servicoAprovado),
                List.of(servicoRecusado),
                List.of());

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> { os.setStatus(invocation.getArgument(1)); return null; })
                .when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemDeServico result = cancelOSService.execute(1L);

        assertEquals(StatusOS.CANCELADA, result.getStatus());
        assertEquals(0, pecaAprovada.getEstoqueReservado());
        assertEquals(2, pecaRecusada.getEstoqueReservado());
        verify(updatePecaGateway, times(1)).execute(pecaAprovada);
        verify(updatePecaGateway, never()).execute(pecaRecusada);
    }

    @Test
    public void cancelFromEmExecucaoReleasesOnlyApproved() throws GatewayException, ValidationException, ServiceException {
        Peca peca = pecaComEstoqueReservado(2);
        ServicoOS servicoAprovado = servicoComAprovado(peca, true);

        OrdemDeServico os = osComStatus(StatusOS.EM_EXECUCAO,
                List.of(servicoAprovado),
                List.of(),
                List.of());

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> { os.setStatus(invocation.getArgument(1)); return null; })
                .when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemDeServico result = cancelOSService.execute(1L);

        assertEquals(StatusOS.CANCELADA, result.getStatus());
        assertEquals(0, peca.getEstoqueReservado());
        verify(updatePecaGateway, times(1)).execute(peca);
    }

    @Test
    public void cancelReleasesAcrossAllThreeLists() throws GatewayException, ValidationException, ServiceException {
        Peca pecaDesejado = pecaComEstoqueReservado(1);
        Peca pecaNecessario = pecaComEstoqueReservado(2);
        Peca pecaAdicional = pecaComEstoqueReservado(1);

        OrdemDeServico os = osComStatus(StatusOS.RECEBIDA,
                List.of(servicoSoPecas(pecaDesejado)),
                List.of(servicoSoPecas(pecaNecessario)),
                List.of(servicoSoPecas(pecaAdicional)));

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doAnswer(invocation -> { os.setStatus(invocation.getArgument(1)); return null; })
                .when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));
        when(updateOSGateway.execute(any(OrdemDeServico.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(updatePecaGateway.execute(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cancelOSService.execute(1L);

        assertEquals(0, pecaDesejado.getEstoqueReservado());
        assertEquals(0, pecaNecessario.getEstoqueReservado());
        assertEquals(0, pecaAdicional.getEstoqueReservado());
        verify(updatePecaGateway, times(3)).execute(any(Peca.class));
    }

    @Test
    public void onGatewayException() throws GatewayException {
        doThrow(GatewayException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(GatewayException.class, () -> cancelOSService.execute(1L));
    }

    @Test
    public void onValidationException() throws GatewayException, ValidationException, ServiceException {
        OrdemDeServico os = OrdemDeServicoFixture.recebida(1L);

        when(findOSGateway.execute(any(Long.class))).thenReturn(os);
        doThrow(ValidationException.class).when(updateOSStatusService).execute(any(OrdemDeServico.class), any(StatusOS.class));

        assertThrows(ValidationException.class, () -> cancelOSService.execute(1L));

        verifyNoInteractions(updatePecaGateway, updateInsumoGateway, updateOSGateway);
    }

    @Test
    public void onUnexpectedException() throws GatewayException {
        doThrow(RuntimeException.class).when(findOSGateway).execute(any(Long.class));

        assertThrows(ServiceException.class, () -> cancelOSService.execute(1L));
    }

    private OrdemDeServico osComStatus(StatusOS status,
                                       List<ServicoOS> desejados,
                                       List<ServicoOS> necessarios,
                                       List<ServicoOS> adicionais) {
        return OrdemDeServico.builder()
                .id(1L)
                .status(status)
                .cliente(OrdemDeServicoFixture.clienteJoaoSilva())
                .veiculo(OrdemDeServicoFixture.veiculoCorollaABC1D23())
                .servicosDesejados(new ArrayList<>(desejados))
                .servicosNecessarios(new ArrayList<>(necessarios))
                .servicosAdicionais(new ArrayList<>(adicionais))
                .build();
    }

    private ServicoOS servicoComProdutos(Peca peca, Insumo insumo) {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Revisão de freios").build())
                .aprovado(null)
                .pecas(new ArrayList<>(List.of(
                        PecaOS.builder().peca(peca).quantidade(peca.getEstoqueReservado()).precoTotal(BigDecimal.TEN).build()
                )))
                .insumos(new ArrayList<>(List.of(
                        InsumoOS.builder().insumo(insumo).quantidade(insumo.getEstoqueReservado()).precoTotal(BigDecimal.TEN).build()
                )))
                .build();
    }

    private ServicoOS servicoSoPecas(Peca peca) {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Balanceamento").build())
                .aprovado(null)
                .pecas(new ArrayList<>(List.of(
                        PecaOS.builder().peca(peca).quantidade(peca.getEstoqueReservado()).precoTotal(BigDecimal.TEN).build()
                )))
                .insumos(new ArrayList<>())
                .build();
    }

    private ServicoOS servicoComAprovado(Peca peca, boolean aprovado) {
        return ServicoOS.builder()
                .servico(Servico.builder().nome("Serviço").build())
                .aprovado(aprovado)
                .pecas(new ArrayList<>(List.of(
                        PecaOS.builder().peca(peca).quantidade(peca.getEstoqueReservado()).precoTotal(BigDecimal.TEN).build()
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