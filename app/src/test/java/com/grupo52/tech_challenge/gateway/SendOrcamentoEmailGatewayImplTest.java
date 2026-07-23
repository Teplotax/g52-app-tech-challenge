package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.impl.SendOrcamentoEmailGatewayImpl;
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

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendOrcamentoEmailGatewayImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private OrcamentoDocumentGateway orcamentoDocumentGateway;

    @InjectMocks
    private SendOrcamentoEmailGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(gateway, "from", "oficina@exemplo.com");
    }

    private MimeMessage realMimeMessage() {
        Session session = Session.getInstance(new Properties());
        return new MimeMessage(session);
    }

    private Ordem osComEmail(String email) {
        return Ordem.builder()
                .id(1L)
                .cliente(Cliente.builder().nomeSocial("João Silva").email(email).build())
                .build();
    }

    @Test
    void executeSucesso() throws Exception {
        Ordem os = osComEmail("joao.silva@email.com");

        when(mailSender.createMimeMessage()).thenReturn(realMimeMessage());
        when(orcamentoDocumentGateway.buildHtml(os)).thenReturn("<html>Orçamento</html>");
        when(orcamentoDocumentGateway.buildPdf(os)).thenReturn(new byte[]{1, 2, 3});

        gateway.execute(os);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void executeClienteSemEmailLancaGatewayException() {
        Ordem os = osComEmail(null);

        assertThrows(GatewayException.class, () -> gateway.execute(os));

        verifyNoInteractions(mailSender, orcamentoDocumentGateway);
    }

    @Test
    void executeClienteComEmailEmBrancoLancaGatewayException() {
        Ordem os = osComEmail("   ");

        assertThrows(GatewayException.class, () -> gateway.execute(os));

        verifyNoInteractions(mailSender, orcamentoDocumentGateway);
    }

    @Test
    void executeErroAoConstruirPdfPropagaGatewayException() throws Exception {
        Ordem os = osComEmail("joao.silva@email.com");

        when(mailSender.createMimeMessage()).thenReturn(realMimeMessage());
        when(orcamentoDocumentGateway.buildHtml(os)).thenReturn("<html>Orçamento</html>");
        when(orcamentoDocumentGateway.buildPdf(os)).thenThrow(new GatewayException("Falha ao gerar PDF"));

        assertThrows(GatewayException.class, () -> gateway.execute(os));

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void executeErroInesperadoAoEnviarLancaGatewayException() throws Exception {
        Ordem os = osComEmail("joao.silva@email.com");

        when(mailSender.createMimeMessage()).thenReturn(realMimeMessage());
        when(orcamentoDocumentGateway.buildHtml(os)).thenReturn("<html>Orçamento</html>");
        when(orcamentoDocumentGateway.buildPdf(os)).thenReturn(new byte[]{1, 2, 3});
        doThrow(new RuntimeException("smtp indisponível")).when(mailSender).send(any(MimeMessage.class));

        assertThrows(GatewayException.class, () -> gateway.execute(os));
    }
}