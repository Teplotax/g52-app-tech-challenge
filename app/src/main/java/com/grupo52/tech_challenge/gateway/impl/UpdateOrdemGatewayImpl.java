package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.UpdateOrdemGateway;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.ServicoOSDatabase;
import com.grupo52.tech_challenge.gateway.database.model.StatusChangeDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.OrdemDeServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateOrdemGatewayImpl implements UpdateOrdemGateway {

    private final OrdemDeServicoRepository ordemDeServicoRepository;

    @Override
    @Transactional
    public Ordem execute(Ordem os) throws GatewayException {
        OrdemDeServicoDatabase existing = ordemDeServicoRepository.findById(os.getId())
                .orElseThrow(() -> new GatewayException("OrdemDeServico não encontrada: id=" + os.getId()));

        OrdemDeServicoDatabase updated = OrdemDeServicoDatabase.builder()
                .id(existing.getId())
                .cliente(existing.getCliente())
                .veiculo(existing.getVeiculo())
                .status(os.getStatus() != null ? os.getStatus() : existing.getStatus())
                .complexidade(os.getComplexidade() != null ? os.getComplexidade() : existing.getComplexidade())
                .sintomas(os.getSintomas() != null ? os.getSintomas() : existing.getSintomas())
                .tagChave(os.getTagChave() != null ? os.getTagChave() : existing.getTagChave())
                .precoTotal(os.getPrecoTotal() != null ? os.getPrecoTotal() : existing.getPrecoTotal())
                .precoServicosDesejados(os.getPrecoServicosDesejados() != null ? os.getPrecoServicosDesejados() : existing.getPrecoServicosDesejados())
                .precoServicosNecessarios(os.getPrecoServicosNecessarios() != null ? os.getPrecoServicosNecessarios() : existing.getPrecoServicosNecessarios())
                .precoServicosAdicionais(os.getPrecoServicosAdicionais() != null ? os.getPrecoServicosAdicionais() : existing.getPrecoServicosAdicionais())
                .justificativaNecessarios(os.getJustificativaNecessarios() != null ? os.getJustificativaNecessarios() : existing.getJustificativaNecessarios())
                .justificativaAdicionais(os.getJustificativaAdicionais() != null ? os.getJustificativaAdicionais() : existing.getJustificativaAdicionais())
                .createdAt(existing.getCreatedAt())
                .build();

        mergeServicos(updated, os, existing);

        return ordemDeServicoRepository.save(updated).toDomain();
    }

    private void mergeServicos(OrdemDeServicoDatabase entity, Ordem os, OrdemDeServicoDatabase existing) {
        if (os.getServicosDesejados() != null) {
            List<ServicoOSDatabase> list = os.getServicosDesejados().stream()
                    .map(s -> ServicoOSDatabase.fromDomain(s, entity, ServicoOSDatabase.TipoServicoOS.DESEJADO))
                    .toList();
            entity.getServicosDesejados().clear();
            entity.getServicosDesejados().addAll(list);
        }

        if (os.getServicosNecessarios() != null) {
            List<ServicoOSDatabase> list = os.getServicosNecessarios().stream()
                    .map(s -> ServicoOSDatabase.fromDomain(s, entity, ServicoOSDatabase.TipoServicoOS.NECESSARIO))
                    .toList();
            entity.getServicosNecessarios().clear();
            entity.getServicosNecessarios().addAll(list);
        }

        if (os.getServicosAdicionais() != null) {
            List<ServicoOSDatabase> list = os.getServicosAdicionais().stream()
                    .map(s -> ServicoOSDatabase.fromDomain(s, entity, ServicoOSDatabase.TipoServicoOS.ADICIONAL))
                    .toList();
            entity.getServicosAdicionais().clear();
            entity.getServicosAdicionais().addAll(list);
        }

        entity.getHistorico().addAll(existing.getHistorico());
        if (os.getHistorico() != null) {
            os.getHistorico().stream()
                    .filter(sc -> sc.getId() == null)
                    .map(sc -> StatusChangeDatabase.fromDomain(sc, entity))
                    .forEach(sc -> entity.getHistorico().add(sc));
        }
    }
}