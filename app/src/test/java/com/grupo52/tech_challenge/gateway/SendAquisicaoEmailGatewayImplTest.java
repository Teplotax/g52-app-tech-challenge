package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemInsumo;
import com.grupo52.tech_challenge.domain.OrdemPeca;
import com.grupo52.tech_challenge.domain.OrdemServico;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.impl.SendAquisicaoEmailGatewayImpl;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendAquisicaoEmailGatewayImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ApprovalTokenGateway approvalTokenGateway;

    @InjectMocks
    private SendAquisicaoEmailGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(gateway, "from", "oficina@exemplo.com");
        ReflectionTestUtils.setField(gateway, "baseUrl", "https://oficina.exemplo.com");
    }

    private MimeMessage realMimeMessage() {
        Session session = Session.getInstance(new Properties());
        return new MimeMessage(session);
    }

    private Ordem osComItensNaoReservados() {
        Peca peca = Peca.builder().id(100L).nome("Disco de freio dianteiro").preco(new BigDecimal("90.00")).build();
        Insumo insumo = Insumo.builder().id(200L).nome("Fluido de freio DOT 4").preco(new BigDecimal("25.00")).build();

        OrdemServico servico = OrdemServico.builder()
                .servico(Servico.builder().nome("Troca de disco").build())
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
                .servicosDesejados(new ArrayList<>(List.of(servico)))
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();
    }

    @Test
    void executeSucesso() throws GatewayException {
        Ordem os = osComItensNaoReservados();

        when(approvalTokenGateway.generate(1L)).thenReturn("token-abc");
        when(mailSender.createMimeMessage()).thenReturn(realMimeMessage());

        gateway.execute(os);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void buildHtmlIncluiItensNaoReservadosComQuantidade() {
        Ordem os = osComItensNaoReservados();
        when(approvalTokenGateway.generate(1L)).thenReturn("token-abc");

        String html = gateway.buildHtml(os);

        assertTrue(html.contains("Disco de freio dianteiro"));
        assertTrue(html.contains("Fluido de freio DOT 4"));
        assertTrue(html.contains(">2<"));
        assertTrue(html.contains(">1<"));
    }

    @Test
    void buildHtmlIncluiLinkDeConfirmacaoComTokenGerado() {
        Ordem os = osComItensNaoReservados();
        when(approvalTokenGateway.generate(1L)).thenReturn("token-abc");

        String html = gateway.buildHtml(os);

        assertTrue(html.contains("https://oficina.exemplo.com/aquisicao/1?token=token-abc"));
        verify(approvalTokenGateway, times(1)).generate(1L);
    }

    @Test
    void buildHtmlOmiteItensJaReservados() {
        Peca pecaReservada = Peca.builder().id(101L).nome("Pastilha de freio").preco(new BigDecimal("35.00")).build();
        OrdemServico servico = OrdemServico.builder()
                .servico(Servico.builder().nome("Revisão de freios").build())
                .aprovado(true)
                .pecas(new ArrayList<>(List.of(
                        OrdemPeca.builder().peca(pecaReservada).quantidade(4).precoTotal(new BigDecimal("140.00")).reservado(true).build()
                )))
                .insumos(new ArrayList<>())
                .build();

        Ordem os = Ordem.builder()
                .id(2L)
                .servicosDesejados(new ArrayList<>(List.of(servico)))
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();

        when(approvalTokenGateway.generate(2L)).thenReturn("token-xyz");

        String html = gateway.buildHtml(os);

        assertFalse(html.contains("Pastilha de freio"));
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        Ordem os = osComItensNaoReservados();

        when(approvalTokenGateway.generate(1L)).thenReturn("token-abc");
        when(mailSender.createMimeMessage()).thenReturn(realMimeMessage());
        doThrow(new RuntimeException("smtp indisponível")).when(mailSender).send(any(MimeMessage.class));

        assertThrows(GatewayException.class, () -> gateway.execute(os));
    }
}