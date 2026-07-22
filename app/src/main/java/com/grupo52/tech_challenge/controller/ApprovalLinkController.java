package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.ApprovalLinkApi;
import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemServico;
import com.grupo52.tech_challenge.gateway.ApprovalTokenGateway;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.usecase.ApproveOrdemUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ApprovalLinkController implements ApprovalLinkApi {

    @Autowired
    private ApprovalTokenGateway approvalTokenGateway;

    @Autowired
    private FindOrdemGateway findOrdemGateway;

    @Autowired
    private ApproveOrdemUseCase approveOrdemUseCase;

    @Override
    public ResponseEntity<String> confirmPage(Long osId, String token) {

        if (!approvalTokenGateway.isValid(osId, token)) {
            return html(403, page("Link inválido ou expirado", "Solicite um novo orçamento à oficina.", null));
        }

        try {
            Ordem os = findOrdemGateway.execute(osId);

            if (os.getStatus() == Status.APROVADA) {
                return html(200, page("Orçamento já aprovado", "A ordem de serviço #" + osId + " já estava aprovada.", null));
            }

            String resumo = "Ordem de Serviço #" + osId + " &mdash; Total selecionado: <strong><span id=\"totalDinamico\"></span></strong>";
            String form = buildForm(osId, token, os);

            return html(200, page("Confirmar aprovação", resumo, form));
        } catch (Exception e) {
            return html(500, page("Não foi possível carregar", "Tente novamente mais tarde ou contate a oficina.", null));
        }
    }

    @Override
    public ResponseEntity<String> approve(Long osId, String token, List<Long> servicosAprovados) {

        if (!approvalTokenGateway.isValid(osId, token)) {
            return html(403, page("Link inválido ou expirado", "Solicite um novo orçamento à oficina.", null));
        }

        try {
            Ordem os = findOrdemGateway.execute(osId);

            if (os.getStatus() == Status.APROVADA) {
                return html(200, page("Orçamento já aprovado", "A ordem de serviço #" + osId + " já estava aprovada.", null));
            }

            List<Long> aprovados = servicosAprovados != null ? servicosAprovados : new ArrayList<>();
            if (aprovados.isEmpty()) {
                return html(422, page("Nenhum serviço selecionado", "Selecione ao menos um serviço para confirmar a aprovação.", null));
            }
            approveOrdemUseCase.parcialApprove(osId, aprovados);
            return html(200, page("Orçamento aprovado!", "A ordem de serviço #" + osId + " foi aprovada com sucesso.", null));
        } catch (Exception e) {
            return html(500, page("Não foi possível aprovar", "Tente novamente mais tarde ou contate a oficina.", null));
        }
    }

    private String buildForm(Long osId, String token, Ordem os) {
        StringBuilder sb = new StringBuilder();
        sb.append("<form method=\"POST\" action=\"/aprovacao/").append(osId).append("\">");
        sb.append("<input type=\"hidden\" name=\"token\" value=\"").append(token).append("\"/>");

        if (!os.getServicosDesejados().isEmpty()) {
            sb.append(servicoSection("Serviços Desejados", os.getServicosDesejados(), true));
        }
        if (!os.getServicosNecessarios().isEmpty()) {
            sb.append(servicoSection("Serviços Necessários", os.getServicosNecessarios(), true));
        }
        if (!os.getServicosAdicionais().isEmpty()) {
            sb.append(servicoSection("Serviços Adicionais", os.getServicosAdicionais(), false));
        }

        sb.append("<div style=\"margin-top:28px;display:flex;justify-content:center;gap:12px;\">");
        sb.append("<button type=\"button\" onclick=\"history.back()\" "
                + "style=\"background:#fff;color:#555;padding:12px 28px;border:1px solid #ccc;"
                + "border-radius:6px;font-size:15px;cursor:pointer;\">Cancelar</button>");
        sb.append("<button id=\"btnAprovar\" type=\"submit\" "
                + "style=\"background:#1a7f37;color:#fff;padding:12px 28px;"
                + "border:none;border-radius:6px;font-size:15px;cursor:pointer;\">Confirmar aprovação</button>");
        sb.append("</div>");
        sb.append("</form>");
        return sb.toString();
    }
    private String servicoSection(String titulo, List<OrdemServico> servicos, boolean preChecked) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"margin:20px auto;max-width:480px;text-align:left;\">")
                .append("<h3 style=\"font-size:14px;color:#444;border-bottom:1px solid #ddd;padding-bottom:6px;margin-bottom:12px;\">")
                .append(titulo).append("</h3>");

        for (OrdemServico ordemServico : servicos) {
            String checked = preChecked ? " checked" : "";
            String nomeServico = ordemServico.getServico() != null ? ordemServico.getServico().getNome() : "Serviço #" + ordemServico.getId();
            String preco = ordemServico.getPrecoTotal() != null ? " &mdash; R$ " + scale(ordemServico.getPrecoTotal()) : "";
            String precoRaw = ordemServico.getPrecoTotal() != null ? ordemServico.getPrecoTotal().toPlainString() : "0";

            sb.append("<label style=\"display:flex;align-items:center;gap:10px;margin-bottom:10px;font-size:14px;cursor:pointer;\">")
                    .append("<input type=\"checkbox\" name=\"servicosAprovados\" value=\"").append(ordemServico.getId()).append("\"")
                    .append(" data-preco=\"").append(precoRaw).append("\"")
                    .append(checked).append(" style=\"width:16px;height:16px;\"/>")
                    .append("<span>").append(nomeServico).append(preco).append("</span>")
                    .append("</label>");
        }

        sb.append("</div>");
        return sb.toString();
    }

    private ResponseEntity<String> html(int status, String body) {
        return ResponseEntity.status(status).contentType(new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8)).body(body);
    }

    private String scale(BigDecimal value) {
        return value != null ? value.setScale(2, RoundingMode.HALF_UP).toString() : "0.00";
    }

    private String page(String titulo, String mensagem, String extra) {
        String script = extra != null
                ? "<script>"
                  + "function updateTotal(){"
                  + "var total=0;"
                  + "document.querySelectorAll('input[name=\"servicosAprovados\"]:checked').forEach(function(cb){"
                  + "total+=parseFloat(cb.dataset.preco||0);"
                  + "});"
                  + "var el=document.getElementById('totalDinamico');"
                  + "if(el)el.textContent='R$ '+total.toFixed(2).replace('.',',');"
                  + "var btn=document.getElementById('btnAprovar');"
                  + "if(btn){btn.disabled=total===0;btn.style.opacity=total===0?'0.45':'1';btn.style.cursor=total===0?'not-allowed':'pointer';}"
                  + "}"
                  + "document.addEventListener('DOMContentLoaded',function(){"
                  + "document.querySelectorAll('input[name=\"servicosAprovados\"]').forEach(function(cb){"
                  + "cb.addEventListener('change',updateTotal);"
                  + "});"
                  + "updateTotal();"
                  + "});"
                  + "</script>"
                : "";

        return "<html><head><meta charset=\"UTF-8\"/><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"/>"
                + script
                + "</head>"
                + "<body style=\"font-family:Arial,sans-serif;text-align:center;padding:48px 16px;color:#222;\">"
                + "<h1 style=\"font-size:20px;\">" + titulo + "</h1>"
                + "<p style=\"color:#555;\">" + mensagem + "</p>"
                + (extra != null ? "<div style=\"margin-top:24px;\">" + extra + "</div>" : "")
                + "</body></html>";
    }
}