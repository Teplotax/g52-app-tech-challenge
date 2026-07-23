package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.OrdemInsumo;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemPeca;
import com.grupo52.tech_challenge.domain.OrdemServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ApprovalTokenGateway;
import com.grupo52.tech_challenge.gateway.OrcamentoDocumentGateway;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrcamentoDocumentGatewayImpl implements OrcamentoDocumentGateway {

    private final ApprovalTokenGateway approvalTokenGateway;

    @Value("${app.approval.base-url}")
    private String baseUrl;

    @Override
    public String buildHtml(Ordem os) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset=\"UTF-8\"/><style>")
                .append("body{font-family:Arial,sans-serif;color:#222;}")
                .append("h1{font-size:18px;}h2{font-size:14px;margin-top:20px;}")
                .append("table{width:100%;border-collapse:collapse;margin-top:8px;}")
                .append("th,td{border:1px solid #ccc;padding:6px;text-align:left;font-size:12px;}")
                .append("th{background:#f2f2f2;}.total{font-weight:bold;}")
                .append("</style></head><body>");

        sb.append("<h1>Orçamento - Ordem de Serviço #").append(os.getId()).append("</h1>");
        sb.append("<p><strong>Cliente:</strong> ").append(safe(os.getCliente().getNomeSocial())).append("<br/>");
        sb.append("<strong>Documento:</strong> ").append(safe(os.getCliente().getDocumento())).append("<br/>");
        sb.append("<strong>Veículo:</strong> ").append(safe(os.getVeiculo().getPlaca())).append("</p>");

        if (os.getSintomas() != null) {
            sb.append("<p><strong>Sintomas:</strong> ").append(safe(os.getSintomas())).append("</p>");
        }

        appendServicoSection(sb, "Serviços desejados", os.getServicosDesejados(), null, false);
        appendServicoSection(sb, "Serviços necessários", os.getServicosNecessarios(), os.getJustificativaNecessarios(), true);
        appendServicoSection(sb, "Serviços adicionais", os.getServicosAdicionais(), os.getJustificativaAdicionais(), true);

        sb.append("<h2>Resumo</h2><table>");
        appendResumoRow(sb, "Serviços desejados", os.getPrecoServicosDesejados());
        appendResumoRow(sb, "Serviços necessários", os.getPrecoServicosNecessarios());
        appendResumoRow(sb, "Serviços adicionais", os.getPrecoServicosAdicionais());
        sb.append("<tr class=\"total\"><td>Total</td><td>R$ ").append(scale(os.getPrecoTotal())).append("</td></tr>");
        sb.append("</table>");

        String token = approvalTokenGateway.generate(os.getId());
        String approvalUrl = baseUrl + "/aprovacao/" + os.getId() + "?token=" + token;
        sb.append("<div style=\"text-align:center;margin-top:24px;\">")
                .append("<a href=\"").append(approvalUrl).append("\" ")
                .append("style=\"background:#1a7f37;color:#fff;padding:12px 24px;")
                .append("text-decoration:none;border-radius:6px;display:inline-block;font-size:14px;\">")
                .append("Revisar e aprovar serviços</a></div>");

        sb.append("<p style=\"margin-top:24px;font-size:11px;color:#666;\">")
                .append("Este é um orçamento sujeito à sua aprovação. Clique no botão acima para revisar os serviços e selecionar quais deseja aprovar.")
                .append("</p>");

        sb.append("</body></html>");
        return sb.toString();
    }

    @Override
    public byte[] buildPdf(Ordem os) throws GatewayException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(buildPdfHtml(os), null);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new GatewayException("Falha ao gerar PDF do orçamento: " + e.getMessage(), e);
        }
    }

    private String buildPdfHtml(Ordem os) {
        String html = buildHtml(os);
        int divStart = html.indexOf("<div style=\"text-align:center;margin-top:24px;\">");
        if (divStart < 0) {
            return html;
        }
        int divEnd = html.indexOf("</div>", divStart);
        if (divEnd < 0) {
            return html;
        }
        return html.substring(0, divStart) + html.substring(divEnd + "</div>".length());
    }

    private void appendServicoSection(StringBuilder sb, String titulo, List<OrdemServico> servicos, String justificativa, boolean showJustificativa) {
        if (servicos == null || servicos.isEmpty()) {
            return;
        }
        sb.append("<h2>").append(titulo).append("</h2>");
        if (showJustificativa && justificativa != null && !justificativa.isBlank()) {
            sb.append("<p><em>").append(safe(justificativa)).append("</em></p>");
        }
        sb.append("<table><tr><th>Serviço</th><th>Mão de obra</th><th>Peças/Insumos</th><th>Total</th></tr>");
        for (OrdemServico servico : servicos) {
            sb.append("<tr><td>").append(safe(servico.getServico().getNome())).append("</td>");
            sb.append("<td>R$ ").append(scale(servico.getPrecoHorasTecnicas())).append("</td>");
            sb.append("<td>").append(itensDescricao(servico)).append("</td>");
            sb.append("<td>R$ ").append(scale(servico.getPrecoTotal())).append("</td></tr>");
        }
        sb.append("</table>");
    }

    private String itensDescricao(OrdemServico servico) {
        StringBuilder itens = new StringBuilder();
        if (servico.getPecas() != null) {
            for (OrdemPeca peca : servico.getPecas()) {
                itens.append(peca.getQuantidade()).append("x ").append(safe(peca.getPeca().getNome())).append("<br/>");
            }
        }
        if (servico.getInsumos() != null) {
            for (OrdemInsumo insumo : servico.getInsumos()) {
                itens.append(insumo.getQuantidade()).append("x ").append(safe(insumo.getInsumo().getNome())).append("<br/>");
            }
        }
        return itens.length() == 0 ? "-" : itens.toString();
    }

    private void appendResumoRow(StringBuilder sb, String label, BigDecimal valor) {
        sb.append("<tr><td>").append(label).append("</td><td>R$ ").append(scale(valor)).append("</td></tr>");
    }

    private String scale(BigDecimal value) {
        return value != null ? value.setScale(2, RoundingMode.HALF_UP).toString() : "0.00";
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}