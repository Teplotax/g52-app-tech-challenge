package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
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

            String resumo = "Total do orçamento: R$ " + scale(os.getPrecoTotal());
            String form = "<form method=\"POST\" action=\"/aprovacao/" + osId + "\">"
                    + "<input type=\"hidden\" name=\"token\" value=\"" + token + "\"/>"
                    + "<button type=\"submit\" style=\"background:#1a7f37;color:#fff;padding:12px 24px;"
                    + "border:none;border-radius:6px;font-size:14px;cursor:pointer;\">Confirmar aprovação</button>"
                    + "</form>";

            return html(200, page("Confirmar aprovação", resumo, form));
        } catch (Exception e) {
            return html(500, page("Não foi possível carregar", "Tente novamente mais tarde ou contate a oficina.", null));
        }
    }

    @PostMapping("/{osId}")
    public ResponseEntity<String> approve(
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

            approveOSService.approveAll(osId);
            return html(200, page("Orçamento aprovado!", "A ordem de serviço #" + osId + " foi aprovada com sucesso.", null));
        } catch (Exception e) {
            return html(500, page("Não foi possível aprovar", "Tente novamente mais tarde ou contate a oficina.", null));
        }
    }

    private ResponseEntity<String> html(int status, String body) {
        return ResponseEntity.status(status).contentType(MediaType.TEXT_HTML).body(body);
    }

    private String scale(BigDecimal value) {
        return value != null ? value.setScale(2, RoundingMode.HALF_UP).toString() : "0.00";
    }

    private String page(String titulo, String mensagem, String extra) {
        return "<html><head><meta charset=\"UTF-8\"/><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"/></head>"
                + "<body style=\"font-family:Arial,sans-serif;text-align:center;padding:48px 16px;color:#222;\">"
                + "<h1 style=\"font-size:20px;\">" + titulo + "</h1>"
                + "<p style=\"color:#555;\">" + mensagem + "</p>"
                + (extra != null ? "<div style=\"margin-top:24px;\">" + extra + "</div>" : "")
                + "</body></html>";
    }
}
