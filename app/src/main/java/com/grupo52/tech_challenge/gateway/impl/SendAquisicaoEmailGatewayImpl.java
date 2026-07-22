package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemInsumo;
import com.grupo52.tech_challenge.domain.OrdemPeca;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ApprovalTokenGateway;
import com.grupo52.tech_challenge.gateway.SendAquisicaoEmailGateway;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendAquisicaoEmailGatewayImpl implements SendAquisicaoEmailGateway {

    private static final String DESTINATARIO = "almoxarifado@teplocar.com.br";

    private final JavaMailSender mailSender;
    private final ApprovalTokenGateway approvalTokenGateway;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.approval.base-url}")
    private String baseUrl;

    @Override
    public void execute(Ordem os) throws GatewayException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(DESTINATARIO);
            helper.setSubject("Aquisição necessária - Ordem de Serviço #" + os.getId());
            helper.setText(buildHtml(os), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new GatewayException("Falha ao enviar email de aquisição para OS id=" + os.getId(), e);
        }
    }

    public String buildHtml(Ordem os) {
        String token = approvalTokenGateway.generate(os.getId());
        String confirmUrl = baseUrl + "/aquisicao/" + os.getId() + "?token=" + token;

        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset=\"UTF-8\"/><style>")
                .append("body{font-family:Arial,sans-serif;color:#222;}")
                .append("h1{font-size:18px;}")
                .append("table{width:100%;border-collapse:collapse;margin-top:8px;}")
                .append("th,td{border:1px solid #ccc;padding:6px;text-align:left;font-size:12px;}")
                .append("th{background:#f2f2f2;}")
                .append("</style></head><body>");

        sb.append("<h1>Aquisição necessária - Ordem de Serviço #").append(os.getId()).append("</h1>");
        sb.append("<p>Os itens abaixo não possuem estoque suficiente para reserva e precisam ser adquiridos:</p>");

        sb.append("<table><tr><th>Item</th><th>Quantidade necessária</th></tr>");
        for (OrdemPeca ordemPeca : os.getPecasNaoReservadas()) {
            sb.append("<tr><td>").append(safe(ordemPeca.getPeca().getNome())).append("</td>")
                    .append("<td>").append(ordemPeca.getQuantidade()).append("</td></tr>");
        }
        for (OrdemInsumo ordemInsumo : os.getInsumosNaoReservados()) {
            sb.append("<tr><td>").append(safe(ordemInsumo.getInsumo().getNome())).append("</td>")
                    .append("<td>").append(ordemInsumo.getQuantidade()).append("</td></tr>");
        }
        sb.append("</table>");

        sb.append("<div style=\"text-align:center;margin-top:24px;\">")
                .append("<a href=\"").append(confirmUrl).append("\" ")
                .append("style=\"background:#1a7f37;color:#fff;padding:12px 24px;")
                .append("text-decoration:none;border-radius:6px;display:inline-block;font-size:14px;\">")
                .append("Confirmar aquisição</a></div>");

        sb.append("<p style=\"margin-top:24px;font-size:11px;color:#666;\">")
                .append("Assim que os itens forem adquiridos, clique no botão acima para confirmar a entrada no estoque e liberar a OS para execução.")
                .append("</p>");

        sb.append("</body></html>");
        return sb.toString();
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}