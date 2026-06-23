package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.domain.ServicoOS;
import com.grupo52.tech_challenge.gateway.ApprovalTokenGateway;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.service.ApproveOSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/aprovacao")
public class ApprovalLinkController {

    @Autowired
    private ApprovalTokenGateway approvalTokenGateway;

    @Autowired
    private FindOSGateway findOSGateway;

    @Autowired
    private ApproveOSService approveOSService;

    @GetMapping("/{osId}")
    public ResponseEntity<String> confirmPage(
            @PathVariable Long osId,
            @RequestParam String token) {

        if (!approvalTokenGateway.isValid(osId, token)) {
            return html(403, page("Link inválido ou expirado", "Solicite um novo orçamento à oficina.", null));
        }

        try {
            OrdemDeServico os = findOSGateway.execute(osId);

            if (os.getStatus() == StatusOS.APROVADA) {
                return html(200, page("Orçamento já aprovado", "A ordem de serviço #" + osId + " já estava aprovada.", null));
            }

            String resumo = "Ordem de Serviço #" + osId + " &mdash; Total selecionado: <strong><span id=\"totalDinamico\"></span></strong>";
            String form = buildForm(osId, token, os);

            return html(200, page("Confirmar aprovação", resumo, form));
        } catch (Exception e) {
            return html(500, page("Não foi possível carregar", "Tente novamente mais tarde ou contate a oficina.", null));
        }
    }

    @PostMapping("/{osId}")
    public ResponseEntity<String> approve(
            @PathVariable Long osId,
            @RequestParam String token,
            @RequestParam(name = "servicosAprovados", required = false) List<Long> servicosAprovados) {

        if (!approvalTokenGateway.isValid(osId, token)) {
            return html(403, page("Link inválido ou expirado", "Solicite um novo orçamento à oficina.", null));
        }

        try {
            OrdemDeServico os = findOSGateway.execute(osId);

            if (os.getStatus() == StatusOS.APROVADA) {
                return html(200, page("Orçamento já aprovado", "A ordem de serviço #" + osId + " já estava aprovada.", null));
            }

            List<Long> aprovados = servicosAprovados != null ? servicosAprovados : new ArrayList<>();
            approveOSService.parcialApprove(osId, aprovados);
            return html(200, page("Orçamento aprovado!", "A ordem de serviço #" + osId + " foi aprovada com sucesso.", null));
        } catch (Exception e) {
            return html(500, page("Não foi possível aprovar", "Tente novamente mais tarde ou contate a oficina.", null));
        }
    }

    private String buildForm(Long osId, String token, OrdemDeServico os) {
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

        sb.append("<div style=\"margin-top:28px;\">");
        sb.append("<button type=\"submit\" style=\"background:#1a7f37;color:#fff;padding:12px 28px;")
                .append("border:none;border-radius:6px;font-size:15px;cursor:pointer;\">Confirmar aprovação</button>");
        sb.append("</div>");
        sb.append("</form>");
        return sb.toString();
    }

    private String servicoSection(String titulo, List<ServicoOS> servicos, boolean preChecked) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"margin:20px auto;max-width:480px;text-align:left;\">")
                .append("<h3 style=\"font-size:14px;color:#444;border-bottom:1px solid #ddd;padding-bottom:6px;margin-bottom:12px;\">")
                .append(titulo).append("</h3>");

        for (ServicoOS servicoOS : servicos) {
            String checked = preChecked ? " checked" : "";
            String nomeServico = servicoOS.getServico() != null ? servicoOS.getServico().getNome() : "Serviço #" + servicoOS.getId();
            String preco = servicoOS.getPrecoTotal() != null ? " &mdash; R$ " + scale(servicoOS.getPrecoTotal()) : "";
            String precoRaw = servicoOS.getPrecoTotal() != null ? servicoOS.getPrecoTotal().toPlainString() : "0";

            sb.append("<label style=\"display:flex;align-items:center;gap:10px;margin-bottom:10px;font-size:14px;cursor:pointer;\">")
                    .append("<input type=\"checkbox\" name=\"servicosAprovados\" value=\"").append(servicoOS.getId()).append("\"")
                    .append(" data-preco=\"").append(precoRaw).append("\"")
                    .append(checked).append(" style=\"width:16px;height:16px;\"/>")
                    .append("<span>").append(nomeServico).append(preco).append("</span>")
                    .append("</label>");
        }

        sb.append("</div>");
        return sb.toString();
    }

    private ResponseEntity<String> html(int status, String body) {
        return ResponseEntity.status(status).contentType(MediaType.TEXT_HTML).body(body);
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