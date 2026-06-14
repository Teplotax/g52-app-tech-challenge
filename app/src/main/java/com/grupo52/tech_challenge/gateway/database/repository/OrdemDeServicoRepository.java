package com.grupo52.tech_challenge.gateway.database.repository;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoDatabase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdemDeServicoRepository extends JpaRepository<OrdemDeServicoDatabase, Long> {

    List<OrdemDeServicoDatabase> findByClienteId(Long clienteId);

    List<OrdemDeServicoDatabase> findByVeiculoId(Long veiculoId);

    List<OrdemDeServicoDatabase> findByStatus(StatusOS status);

    List<OrdemDeServicoDatabase> findByClienteIdAndStatus(Long clienteId, StatusOS status);

    List<OrdemDeServicoDatabase> findByTagChave( String tagChave);
}