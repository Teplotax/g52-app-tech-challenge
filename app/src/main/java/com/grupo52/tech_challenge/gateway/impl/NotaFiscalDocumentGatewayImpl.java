package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.domain.ServicoOS;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.NotaFiscalDocumentGateway;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class NotaFiscalDocumentGatewayImpl implements NotaFiscalDocumentGateway {

    @Override
    public String buildHtml(OrdemDeServico os) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset=\"UTF-8\"/><style>")
                .append("body{font-family:Arial,sans-serif;color:#222;}")
                .append("h1{font-size:18px;}h2{font-size:14px;margin-top:20px;}")
                .append("table{width:100%;border-collapse:collapse;margin-top:8px;}")
                .append("th,td{border:1px solid #ccc;padding:6px;text-align:left;font-size:12px;}")
                .append("th{background:#f2f2f2;}.total{font-weight:bold;}")
                .append("</style></head><body>");

        sb.append("<h1>Nota Fiscal - Ordem de Serviço #").append(os.getId()).append("</h1>");
        sb.append("<p><strong>Cliente:</strong> ").append(os.getCliente().getNomeSocial()).append("</p>");
        sb.append("<p><strong>Documento:</strong> ").append(os.getCliente().getDocumento()).append("</p>");
        sb.append("<p><strong>Veículo:</strong> ").append(os.getVeiculo().getPlaca()).append("</p>");

        appendServicosTable(sb, "Serviços Desejados", os.getServicosDesejados());
        appendServicosTable(sb, "Serviços Necessários", os.getServicosNecessarios());
        appendServicosTable(sb, "Serviços Adicionais", os.getServicosAdicionais());

        sb.append("<table><tr class=\"total\">")
                .append("<td>Total Aprovado</td>")
                .append("<td>R$ ").append(scale(os.getPrecoTotal())).append("</td>")
                .append("</tr></table>");

        sb.append("<p style=\"color:#888;font-size:10px;margin-top:32px;\">")
                .append("Documento fictício emitido para fins de demonstração.")
                .append("</p>");

        sb.append("</body></html>");
        return sb.toString();
    }

    @Override
    public byte[] buildPdf(OrdemDeServico os) throws GatewayException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(buildHtml(os), null);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new GatewayException("Falha ao gerar PDF da nota fiscal para OS id=" + os.getId(), e);
        }
    }

    private void appendServicosTable(StringBuilder sb, String titulo, List<ServicoOS> servicos) {
        if (servicos == null || servicos.isEmpty()) return;
        boolean hasApproved = servicos.stream().anyMatch(s -> Boolean.TRUE.equals(s.getAprovado()));
        if (!hasApproved) return;

        sb.append("<h2>").append(titulo).append("</h2>");
        sb.append("<table><tr><th>Serviço</th><th>Valor</th></tr>");
        for (ServicoOS s : servicos) {
            if (Boolean.TRUE.equals(s.getAprovado())) {
                sb.append("<tr><td>").append(s.getServico().getNome()).append("</td>")
                        .append("<td>R$ ").append(scale(s.getPrecoTotal())).append("</td></tr>");
            }
        }
        sb.append("</table>");
    }

    private String scale(BigDecimal value) {
        return value != null ? value.setScale(2, RoundingMode.HALF_UP).toString() : "0.00";
    }
}