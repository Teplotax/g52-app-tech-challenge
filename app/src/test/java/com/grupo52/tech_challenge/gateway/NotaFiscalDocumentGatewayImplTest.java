package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemServico;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.impl.NotaFiscalDocumentGatewayImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotaFiscalDocumentGatewayImplTest {

    private final NotaFiscalDocumentGatewayImpl gateway = new NotaFiscalDocumentGatewayImpl();

    private Ordem osComServicoAprovado() {
        OrdemServico aprovado = OrdemServico.builder()
                .servico(Servico.builder().nome("Troca de óleo").build())
                .aprovado(true)
                .precoTotal(new BigDecimal("150.00"))
                .build();

        return Ordem.builder()
                .id(1L)
                .cliente(Cliente.builder().nomeSocial("João Silva").documento("123.456.789-00").build())
                .veiculo(Veiculo.builder().placa("ABC1D23").build())
                .servicosDesejados(List.of(aprovado))
                .precoTotal(new BigDecimal("150.00"))
                .build();
    }

    @Test
    void buildHtmlIncluiDadosDoClienteEVeiculo() {
        String html = gateway.buildHtml(osComServicoAprovado());

        assertTrue(html.contains("João Silva"));
        assertTrue(html.contains("123.456.789-00"));
        assertTrue(html.contains("ABC1D23"));
        assertTrue(html.contains("Ordem de Serviço #1"));
    }

    @Test
    void buildHtmlIncluiServicoAprovado() {
        String html = gateway.buildHtml(osComServicoAprovado());

        assertTrue(html.contains("Troca de óleo"));
        assertTrue(html.contains("150.00"));
    }

    @Test
    void buildHtmlOmiteSecaoSemServicosAprovados() {
        OrdemServico naoAprovado = OrdemServico.builder()
                .servico(Servico.builder().nome("Alinhamento").build())
                .aprovado(false)
                .precoTotal(new BigDecimal("80.00"))
                .build();

        Ordem os = Ordem.builder()
                .id(2L)
                .cliente(Cliente.builder().nomeSocial("Maria Souza").documento("987.654.321-00").build())
                .veiculo(Veiculo.builder().placa("XYZ9Z99").build())
                .servicosDesejados(List.of(naoAprovado))
                .precoTotal(BigDecimal.ZERO)
                .build();

        String html = gateway.buildHtml(os);

        assertFalse(html.contains("Alinhamento"));
    }

    @Test
    void buildHtmlTrataPrecoTotalNuloComoZero() {
        Ordem os = Ordem.builder()
                .id(3L)
                .cliente(Cliente.builder().nomeSocial("Carlos").documento("111.111.111-11").build())
                .veiculo(Veiculo.builder().placa("AAA1A11").build())
                .servicosDesejados(List.of())
                .precoTotal(null)
                .build();

        String html = gateway.buildHtml(os);

        assertTrue(html.contains("0.00"));
    }

    @Test
    void buildPdfGeraDocumentoNaoVazio() throws GatewayException {
        byte[] pdf = gateway.buildPdf(osComServicoAprovado());

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
        assertEquals("%PDF", new String(pdf, 0, 4));
    }
}