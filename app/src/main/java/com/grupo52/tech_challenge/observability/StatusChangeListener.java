package com.grupo52.tech_challenge.observability;

import com.grupo52.tech_challenge.gateway.database.model.StatusChangeDatabase;
import jakarta.persistence.PostPersist;

import java.time.Duration;
import java.util.Comparator;

// toda linha nova em status_changes vira um OrdemStatusAlterado, com o tempo que a OS ficou no status anterior
public class StatusChangeListener {

    @PostPersist
    public void aoGravar(StatusChangeDatabase atual) {
        var os = atual.getOrdemDeServico();

        var anterior = os.getHistorico().stream()
                .filter(sc -> sc != atual && sc.getCreatedAt() != null && atual.getCreatedAt() != null)
                .filter(sc -> sc.getCreatedAt().isBefore(atual.getCreatedAt()))
                .max(Comparator.comparing(StatusChangeDatabase::getCreatedAt))
                .orElse(null);

        EventosNegocio.statusAlterado(
                os.getId(),
                atual.getStatus(),
                anterior != null ? anterior.getStatus() : null,
                anterior != null ? Duration.between(anterior.getCreatedAt(), atual.getCreatedAt()) : null
        );
    }
}
