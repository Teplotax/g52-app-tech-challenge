package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Marca;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ListMarcasGateway;
import com.grupo52.tech_challenge.gateway.database.model.MarcaDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.MarcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListMarcasGatewayImpl implements ListMarcasGateway {

    private final MarcaRepository repository;

    public List<Marca> execute() throws GatewayException {
        try {
            List<MarcaDatabase> marcas = repository.findAll();

            return marcas.stream().map(MarcaDatabase::toInfo).toList();

        } catch (Exception e) {
            throw new GatewayException("Falha ao listar marcas", e);
        }
    }
}
