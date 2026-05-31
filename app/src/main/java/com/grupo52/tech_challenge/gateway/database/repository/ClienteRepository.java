package com.grupo52.tech_challenge.gateway.database.repository;

import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteDatabase, Long> {
}
