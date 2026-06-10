package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.UpdateServicoGateway;
import com.grupo52.tech_challenge.gateway.database.model.ServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateServicoGatewayImpl implements UpdateServicoGateway {

    private final ServicoRepository repository;

    @Override
    public Servico execute(Servico servico) throws GatewayException {
        try {
            Optional<ServicoDatabase> optional = repository.findById(servico.getId());
            ServicoDatabase existing = optional.orElseThrow(
                    () -> new NotFoundGatewayException("Serviço não encontrado")
            );

            ServicoDatabase updated = ServicoDatabase.fromDomain(
                    Servico.builder()
                            .id(existing.getId())
                            .nome(servico.getNome() != null ? servico.getNome() : existing.getNome())
                            .horasTecnicas(servico.getHorasTecnicas() != null ? servico.getHorasTecnicas() : existing.getHorasTecnicas())
                            .insumos(servico.getInsumos() != null ? servico.getInsumos() : existing.getInsumos())
                            .pecas(servico.getPecas() != null ? servico.getPecas() : existing.getPecas().stream()
                                    .map(ServicoDatabase.ServicoTipoPecaDatabase::toDomain)
                                    .toList())
                            .build()
            );

            return repository.save(updated).toDomain();
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new GatewayException("Falha ao atualizar Serviço, nome deve ser único", 409);
        } catch (Exception e) {
            throw new GatewayException("Falha ao atualizar Serviço, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}