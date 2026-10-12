package com.grupo52.tech_challenge.observability;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.StatusChangeDatabase;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

class EventosNegocioTest {

    @Test
    void statusAlteradoLevaTempoNoStatusAnterior() {
        Map<String, Object> evento = EventosNegocio.statusAlterado(
                10L, Status.AGUARDANDO_APROVACAO, Status.EM_DIAGNOSTICO, Duration.ofMinutes(90));

        assertThat(evento)
                .containsEntry("osId", 10L)
                .containsEntry("status", "AGUARDANDO_APROVACAO")
                .containsEntry("statusAnterior", "EM_DIAGNOSTICO")
                .containsEntry("segundosNoStatusAnterior", 5400.0);
    }

    @Test
    void criacaoDaOsNaoTemStatusAnterior() {
        Map<String, Object> evento = EventosNegocio.statusAlterado(10L, Status.RECEBIDA, null, null);

        assertThat(evento).containsEntry("status", "RECEBIDA")
                .doesNotContainKeys("statusAnterior", "segundosNoStatusAnterior");
    }

    @Test
    void integracaoFalhaLevaTipoDoErro() {
        Map<String, Object> evento = EventosNegocio.integracaoFalha(
                "EMAIL", "orcamento", 7L, new IllegalStateException("smtp fora"));

        assertThat(evento)
                .containsEntry("integracao", "EMAIL")
                .containsEntry("operacao", "orcamento")
                .containsEntry("osId", 7L)
                .containsEntry("erro", "IllegalStateException")
                .containsEntry("mensagem", "smtp fora");
    }

    @Test
    void listenerCalculaTempoDesdeAUltimaMudanca() {
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 1, 10, 0);
        OrdemDeServicoDatabase os = OrdemDeServicoDatabase.builder().id(5L).historico(new ArrayList<>()).build();

        StatusChangeDatabase recebida = StatusChangeDatabase.builder().id(1L).ordemDeServico(os)
                .status(Status.RECEBIDA).createdAt(inicio).build();
        StatusChangeDatabase diagnostico = StatusChangeDatabase.builder().id(2L).ordemDeServico(os)
                .status(Status.EM_DIAGNOSTICO).createdAt(inicio.plusMinutes(30)).build();
        StatusChangeDatabase aguardando = StatusChangeDatabase.builder().id(3L).ordemDeServico(os)
                .status(Status.AGUARDANDO_APROVACAO).createdAt(inicio.plusMinutes(75)).build();
        os.getHistorico().addAll(List.of(recebida, diagnostico, aguardando));

        try (MockedStatic<EventosNegocio> eventos = mockStatic(EventosNegocio.class)) {
            new StatusChangeListener().aoGravar(aguardando);

            eventos.verify(() -> EventosNegocio.statusAlterado(
                    eq(5L), eq(Status.AGUARDANDO_APROVACAO), eq(Status.EM_DIAGNOSTICO), eq(Duration.ofMinutes(45))));
        }
    }

    @Test
    void listenerSemHistoricoAnteriorMandaSemTempo() {
        OrdemDeServicoDatabase os = OrdemDeServicoDatabase.builder().id(5L).historico(new ArrayList<>()).build();
        StatusChangeDatabase recebida = StatusChangeDatabase.builder().id(1L).ordemDeServico(os)
                .status(Status.RECEBIDA).createdAt(LocalDateTime.now()).build();
        os.getHistorico().add(recebida);

        try (MockedStatic<EventosNegocio> eventos = mockStatic(EventosNegocio.class)) {
            new StatusChangeListener().aoGravar(recebida);

            eventos.verify(() -> EventosNegocio.statusAlterado(eq(5L), eq(Status.RECEBIDA), any(), any()));
        }
    }
}
