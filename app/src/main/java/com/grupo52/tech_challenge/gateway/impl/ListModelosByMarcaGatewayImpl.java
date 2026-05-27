package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ListModelosByMarcaGateway;
import com.grupo52.tech_challenge.gateway.database.model.ModeloDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ModeloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListModelosByMarcaGatewayImpl implements ListModelosByMarcaGateway {

    private final ModeloRepository repository;

    public List<Modelo> execute(Long marcaId) throws GatewayException {
        try {
            List<ModeloDatabase> modelos = repository.findByMarcaId(marcaId);

            return modelos.stream().map(ModeloDatabase::toInfo).toList();

        } catch (Exception e) {
            throw new GatewayException("Falha ao listar modelos por marca, marcaId: " + marcaId, e);
        }
    }
}