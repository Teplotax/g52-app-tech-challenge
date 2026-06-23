package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.OrcamentoDocumentGateway;
import com.grupo52.tech_challenge.gateway.SendOrcamentoEmailGateway;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendOrcamentoEmailGatewayImpl implements SendOrcamentoEmailGateway {

    private final JavaMailSender mailSender;
    private final OrcamentoDocumentGateway orcamentoDocumentGateway;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void execute(OrdemDeServico os) throws GatewayException {
        String destinatario = os.getCliente().getEmail();
        System.out.println("=================================");
        System.out.println("Destinatário: " + destinatario);
        System.out.println("=================================");

        if (destinatario == null || destinatario.isBlank()) {
            throw new GatewayException("Cliente sem email cadastrado para OS id=" + os.getId());
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(destinatario);
            helper.setSubject("Orçamento - Ordem de Serviço #" + os.getId());
            helper.setText(orcamentoDocumentGateway.buildHtml(os), true);

            byte[] pdf = orcamentoDocumentGateway.buildPdf(os);
            helper.addAttachment("orcamento-os-" + os.getId() + ".pdf", new ByteArrayResource(pdf));

            mailSender.send(message);
        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao enviar email de orçamento para OS id=" + os.getId(), e);
        }
    }
}
