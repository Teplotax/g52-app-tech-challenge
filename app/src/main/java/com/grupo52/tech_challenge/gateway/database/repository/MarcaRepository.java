package com.grupo52.tech_challenge.gateway.database.repository;

import com.grupo52.tech_challenge.gateway.database.model.MarcaDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcaRepository extends JpaRepository<MarcaDatabase, Long> {
}
