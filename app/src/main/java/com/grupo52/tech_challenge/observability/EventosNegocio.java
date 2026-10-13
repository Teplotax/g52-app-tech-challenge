package com.grupo52.tech_challenge.observability;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.newrelic.api.agent.NewRelic;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

// eventos customizados pros dashboards/alertas do new relic (h7)
public final class EventosNegocio {

    public static final String STATUS_ALTERADO = "OrdemStatusAlterado";
    public static final String INTEGRACAO_FALHA = "IntegracaoFalha";

    private EventosNegocio() {}

    // tempoNoStatusAnterior é null na criação da OS (não tem status anterior)
    public static Map<String, Object> statusAlterado(Long osId, Status status, Status anterior, Duration tempoNoStatusAnterior) {
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("osId", osId);
        atributos.put("status", status.name());
        if (anterior != null) {
            atributos.put("statusAnterior", anterior.name());
        }
        if (tempoNoStatusAnterior != null) {
            atributos.put("segundosNoStatusAnterior", tempoNoStatusAnterior.toMillis() / 1000.0);
        }
        NewRelic.getAgent().getInsights().recordCustomEvent(STATUS_ALTERADO, atributos);
        return atributos;
    }

    // integracao: EMAIL ou PDF; operacao: orcamento, nota_fiscal, aquisicao
    public static Map<String, Object> integracaoFalha(String integracao, String operacao, Long osId, Throwable erro) {
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("integracao", integracao);
        atributos.put("operacao", operacao);
        if (osId != null) {
            atributos.put("osId", osId);
        }
        atributos.put("erro", erro.getClass().getSimpleName());
        atributos.put("mensagem", String.valueOf(erro.getMessage()));
        NewRelic.getAgent().getInsights().recordCustomEvent(INTEGRACAO_FALHA, atributos);
        return atributos;
    }
}
