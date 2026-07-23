package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.AquisicaoLinkApi;
import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemInsumo;
import com.grupo52.tech_challenge.domain.OrdemPeca;
import com.grupo52.tech_challenge.gateway.ApprovalTokenGateway;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.usecase.ConfirmAquisicaoUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
public class AquisicaoLinkController implements AquisicaoLinkApi {

    @Autowired
    private ApprovalTokenGateway approvalTokenGateway;

    @Autowired
    private FindOrdemGateway findOrdemGateway;

    @Autowired
    private ConfirmAquisicaoUseCase confirmAquisicaoUseCase;

    @Override
    public ResponseEntity<String> confirmPage(Long osId, String token) {

        if (!approvalTokenGateway.isValid(osId, token)) {
            return html(403, page("Link inválido ou expirado", "Solicite um novo aviso de aquisição à oficina.", null));
        }

        try {
            Ordem os = findOrdemGateway.execute(osId);

            if (os.getStatus() != Status.AGUARDANDO_AQUISICAO) {
                return html(200, page("Aquisição não pendente", "A ordem de serviço #" + osId + " não está aguardando aquisição de itens.", null));
            }

            String resumo = "Ordem de Serviço #" + osId + " &mdash; confirme abaixo a aquisição dos itens em falta.";
            String form = buildForm(osId, token, os);

            return html(200, page("Confirmar aquisição", resumo, form));
        } catch (Exception e) {
            return html(500, page("Não foi possível carregar", "Tente novamente mais tarde ou contate a oficina.", null));
        }
    }

    @Override
    public ResponseEntity<String> confirm(Long osId, String token) {

        if (!approvalTokenGateway.isValid(osId, token)) {
            return html(403, page("Link inválido ou expirado", "Solicite um novo aviso de aquisição à oficina.", null));
        }

        try {
            Ordem os = findOrdemGateway.execute(osId);

            if (os.getStatus() != Status.AGUARDANDO_AQUISICAO) {
                return html(200, page("Aquisição não pendente", "A ordem de serviço #" + osId + " não está aguardando aquisição de itens.", null));
            }

            confirmAquisicaoUseCase.execute(osId);
            return html(200, page("Aquisição confirmada!", "O estoque foi atualizado e a ordem de serviço #" + osId + " está aprovada.", null));
        } catch (Exception e) {
            return html(500, page("Não foi possível confirmar", "Tente novamente mais tarde ou contate a oficina.", null));
        }
    }

    private String buildForm(Long osId, String token, Ordem os) {
        StringBuilder sb = new StringBuilder();
        sb.append("<form method=\"POST\" action=\"/aquisicao/").append(osId).append("?token=").append(token).append("\">");

        sb.append("<div style=\"margin:20px auto;max-width:480px;text-align:left;\">")
                .append("<h3 style=\"font-size:14px;color:#444;border-bottom:1px solid #ddd;padding-bottom:6px;margin-bottom:12px;\">")
                .append("Itens a adquirir</h3>");

        for (OrdemPeca ordemPeca : os.getPecasNaoReservadas()) {
            sb.append("<div style=\"display:flex;justify-content:space-between;margin-bottom:8px;font-size:14px;\">")
                    .append("<span>").append(ordemPeca.getPeca().getNome()).append("</span>")
                    .append("<strong>").append(ordemPeca.getQuantidade()).append("</strong>")
                    .append("</div>");
        }
        for (OrdemInsumo ordemInsumo : os.getInsumosNaoReservados()) {
            sb.append("<div style=\"display:flex;justify-content:space-between;margin-bottom:8px;font-size:14px;\">")
                    .append("<span>").append(ordemInsumo.getInsumo().getNome()).append("</span>")
                    .append("<strong>").append(ordemInsumo.getQuantidade()).append("</strong>")
                    .append("</div>");
        }
        sb.append("</div>");

        sb.append("<div style=\"margin-top:28px;display:flex;justify-content:center;gap:12px;\">");
        sb.append("<button type=\"button\" onclick=\"history.back()\" "
                + "style=\"background:#fff;color:#555;padding:12px 28px;border:1px solid #ccc;"
                + "border-radius:6px;font-size:15px;cursor:pointer;\">Cancelar</button>");
        sb.append("<button type=\"submit\" "
                + "style=\"background:#1a7f37;color:#fff;padding:12px 28px;"
                + "border:none;border-radius:6px;font-size:15px;cursor:pointer;\">Confirmar aquisição</button>");
        sb.append("</div>");
        sb.append("</form>");
        return sb.toString();
    }

    private ResponseEntity<String> html(int status, String body) {
        return ResponseEntity.status(status).contentType(new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8)).body(body);
    }

    private String page(String titulo, String mensagem, String extra) {
        return "<html><head><meta charset=\"UTF-8\"/><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"/>"
                + "</head>"
                + "<body style=\"font-family:Arial,sans-serif;text-align:center;padding:48px 16px;color:#222;\">"
                + "<h1 style=\"font-size:20px;\">" + titulo + "</h1>"
                + "<p style=\"color:#555;\">" + mensagem + "</p>"
                + (extra != null ? "<div style=\"margin-top:24px;\">" + extra + "</div>" : "")
                + "</body></html>";
    }
}