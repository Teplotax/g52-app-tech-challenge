package com.grupo52.tech_challenge.dto.request;

import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.domain.ServicoOS;
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

    public OrdemDeServico toDomain(Long osId) {
        return OrdemDeServico.builder()
                .id(osId)
                .justificativaNecessarios(this.justificativaNecessarios)
                .justificativaAdicionais(this.justificativaAdicionais)
                .servicosNecessarios(getServicosNecessariosDomain())
                .servicosAdicionais(getServicosAdicionaisDomain())
                .build();
    }

    private List<ServicoOS> getServicosNecessariosDomain() {
        if( this.servicosNecessarios == null ||  this.servicosNecessarios.isEmpty()) {
            return new ArrayList<>();
        }

        return this.servicosNecessarios.stream().map(servicoId -> ServicoOS.builder()
                .servico(Servico.builder().id(servicoId)
                        .build())
                .build()).toList();
    }

    private List<ServicoOS> getServicosAdicionaisDomain() {
        if( this.servicosAdicionais == null ||  this.servicosAdicionais.isEmpty()) {
            return new ArrayList<>();
        }

        return this.servicosAdicionais.stream().map(servicoId -> ServicoOS.builder()
                .servico(Servico.builder().id(servicoId)
                        .build())
                .build()).toList();
    }
}