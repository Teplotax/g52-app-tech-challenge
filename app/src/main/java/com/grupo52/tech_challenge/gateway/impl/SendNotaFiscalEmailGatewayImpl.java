package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.NotaFiscalDocumentGateway;
import com.grupo52.tech_challenge.gateway.SendNotaFiscalEmailGateway;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendNotaFiscalEmailGatewayImpl implements SendNotaFiscalEmailGateway {

    private final JavaMailSender mailSender;
    private final NotaFiscalDocumentGateway notaFiscalDocumentGateway;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void execute(Ordem os) throws GatewayException {
        String destinatario = os.getCliente().getEmail();

        if (destinatario == null || destinatario.isBlank()) {
            throw new GatewayException("Cliente sem email cadastrado para OS id=" + os.getId());
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(destinatario);
            helper.setSubject("Nota Fiscal - Ordem de Serviço #" + os.getId());
            helper.setText(notaFiscalDocumentGateway.buildHtml(os), true);

            byte[] pdf = notaFiscalDocumentGateway.buildPdf(os);
            helper.addAttachment("nota-fiscal-os-" + os.getId() + ".pdf", new ByteArrayResource(pdf));

            mailSender.send(message);
        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao enviar nota fiscal para OS id=" + os.getId(), e);
        }
    }
}