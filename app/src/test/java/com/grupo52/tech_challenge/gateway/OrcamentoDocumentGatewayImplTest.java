package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemServico;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.impl.OrcamentoDocumentGatewayImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoDocumentGatewayImplTest {

    @Mock
    private ApprovalTokenGateway approvalTokenGateway;

    @InjectMocks
    private OrcamentoDocumentGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(gateway, "baseUrl", "https://oficina.exemplo.com");
    }

    private Ordem osBase() {
        OrdemServico necessario = OrdemServico.builder()
                .servico(Servico.builder().nome("Troca de pastilhas").build())
                .precoHorasTecnicas(new BigDecimal("50.00"))
                .precoTotal(new BigDecimal("200.00"))
                .build();

        return Ordem.builder()
                .id(1L)
                .cliente(Cliente.builder().nomeSocial("João Silva").documento("123.456.789-00").build())
                .veiculo(Veiculo.builder().placa("ABC1D23").build())
                .sintomas("Barulho ao frear")
                .servicosNecessarios(List.of(necessario))
                .justificativaNecessarios("Desgaste identificado no diagnóstico")
                .precoServicosNecessarios(new BigDecimal("200.00"))
                .precoTotal(new BigDecimal("200.00"))
                .build();
    }

    @Test
    void buildHtmlIncluiDadosDoClienteEVeiculo() {
        when(approvalTokenGateway.generate(1L)).thenReturn("token-abc");

        String html = gateway.buildHtml(osBase());

        assertTrue(html.contains("João Silva"));
        assertTrue(html.contains("123.456.789-00"));
        assertTrue(html.contains("ABC1D23"));
        assertTrue(html.contains("Barulho ao frear"));
    }

    @Test
    void buildHtmlIncluiLinkDeAprovacaoComTokenGerado() {
        when(approvalTokenGateway.generate(1L)).thenReturn("token-abc");

        String html = gateway.buildHtml(osBase());

        assertTrue(html.contains("https://oficina.exemplo.com/aprovacao/1?token=token-abc"));
        verify(approvalTokenGateway, times(1)).generate(1L);
    }

    @Test
    void buildHtmlIncluiJustificativaDeServicosNecessarios() {
        when(approvalTokenGateway.generate(1L)).thenReturn("token-abc");

        String html = gateway.buildHtml(osBase());

        assertTrue(html.contains("Desgaste identificado no diagnóstico"));
        assertTrue(html.contains("Troca de pastilhas"));
    }

    @Test
    void buildHtmlEscapaCaracteresEspeciais() {
        when(approvalTokenGateway.generate(2L)).thenReturn("token-xyz");

        Ordem os = Ordem.builder()
                .id(2L)
                .cliente(Cliente.builder().nomeSocial("Cliente & Cia <Teste>").documento("000.000.000-00").build())
                .veiculo(Veiculo.builder().placa("ZZZ9Z99").build())
                .precoTotal(BigDecimal.ZERO)
                .build();

        String html = gateway.buildHtml(os);

        assertTrue(html.contains("Cliente &amp; Cia &lt;Teste&gt;"));
        assertFalse(html.contains("Cliente & Cia <Teste>"));
    }

    @Test
    void buildPdfGeraDocumentoNaoVazio() throws GatewayException {
        when(approvalTokenGateway.generate(1L)).thenReturn("token-abc");

        byte[] pdf = gateway.buildPdf(osBase());

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
        assertEquals("%PDF", new String(pdf, 0, 4));
    }
}