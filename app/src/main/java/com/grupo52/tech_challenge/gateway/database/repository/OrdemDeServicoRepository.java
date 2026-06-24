package com.grupo52.tech_challenge.gateway.database.repository;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdemDeServicoRepository extends JpaRepository<OrdemDeServicoDatabase, Long>,
        JpaSpecificationExecutor<OrdemDeServicoDatabase> {

    List<OrdemDeServicoDatabase> findByClienteId(Long clienteId);

    List<OrdemDeServicoDatabase> findByVeiculoId(Long veiculoId);

    List<OrdemDeServicoDatabase> findByStatus(StatusOS status);

    List<OrdemDeServicoDatabase> findByClienteIdAndStatus(Long clienteId, StatusOS status);

    List<OrdemDeServicoDatabase> findByTagChave(String tagChave);

    @Modifying
    @Query("UPDATE OrdemDeServicoDatabase o SET o.tagChave = null WHERE o.id = :osId")
    int clearTagChave(@Param("osId") Long osId);
}