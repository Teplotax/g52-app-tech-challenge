package com.grupo52.tech_challenge.gateway.database.repository;

import com.grupo52.tech_challenge.gateway.database.model.ServicoDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<ServicoDatabase, Long> {

    Optional<ServicoDatabase> findByNome(String nome);

    boolean existsByNome(String nome);
}