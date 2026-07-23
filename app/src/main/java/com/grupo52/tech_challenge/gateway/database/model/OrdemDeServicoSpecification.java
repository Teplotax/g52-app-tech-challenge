package com.grupo52.tech_challenge.gateway.database.model;


import com.grupo52.tech_challenge.domain.Enums.Complexidade;
import com.grupo52.tech_challenge.domain.Enums.Status;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdemDeServicoSpecification {

    private OrdemDeServicoSpecification() {}

    public static Specification<OrdemDeServicoDatabase> withFilters(
            String placa,
            String documentoCliente,
            Status status,
            Complexidade complexidade,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (placa != null && !placa.isBlank()) {
                predicates.add(cb.equal(
                        cb.lower(root.join("veiculo").get("placa")),
                        placa.toLowerCase()
                ));
            }

            if (documentoCliente != null && !documentoCliente.isBlank()) {
                predicates.add(cb.equal(
                        root.join("cliente").get("documento"),
                        documentoCliente
                ));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (complexidade != null) {
                predicates.add(cb.equal(root.get("complexidade"), complexidade));
            }

            if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), dataInicio));
            }

            if (dataFim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), dataFim));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}