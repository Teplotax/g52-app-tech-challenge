package com.grupo52.tech_challenge.gateway.database.repository;

import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<VeiculoDatabase, Long> {
    Optional<VeiculoDatabase> findByPlaca(String placa);
    List<VeiculoDatabase> findByClienteId(Long clienteId);
}
