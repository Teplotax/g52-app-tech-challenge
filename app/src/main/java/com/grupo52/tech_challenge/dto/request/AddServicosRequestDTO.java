package com.grupo52.tech_challenge.dto.request;

import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.domain.OrdemServico;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.grupo52.tech_challenge.validation.annotation.SafeDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@SafeDto
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddServicosRequestDTO {

    private String justificativaNecessarios;

    private String justificativaAdicionais;

    private List<@NotNull @Positive Long> servicosNecessarios;

    private List<@NotNull @Positive Long> servicosAdicionais;

    public Ordem toDomain(Long osId) {
        return Ordem.builder()
                .id(osId)
                .justificativaNecessarios(this.justificativaNecessarios)
                .justificativaAdicionais(this.justificativaAdicionais)
                .servicosNecessarios(getServicosNecessariosDomain())
                .servicosAdicionais(getServicosAdicionaisDomain())
                .build();
    }

    private List<OrdemServico> getServicosNecessariosDomain() {
        if( this.servicosNecessarios == null ||  this.servicosNecessarios.isEmpty()) {
            return new ArrayList<>();
        }

        return this.servicosNecessarios.stream().map(servicoId -> OrdemServico.builder()
                .servico(Servico.builder().id(servicoId)
                        .build())
                .aprovado(false)
                .build()).toList();
    }

    private List<OrdemServico> getServicosAdicionaisDomain() {
        if( this.servicosAdicionais == null ||  this.servicosAdicionais.isEmpty()) {
            return new ArrayList<>();
        }

        return this.servicosAdicionais.stream().map(servicoId -> OrdemServico.builder()
                .servico(Servico.builder().id(servicoId)
                        .build())
                .aprovado(false)
                .build()).toList();
    }
}