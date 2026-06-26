package com.grupo52.tech_challenge.dto.request;

import com.grupo52.tech_challenge.domain.*;
import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.grupo52.tech_challenge.validation.annotation.SafeDto;
import lombok.*;

import java.util.List;

@SafeDto
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOSRequestDTO {

    @NotNull
    @Positive
    private Long clienteId;

    @NotNull
    @Positive
    private Long veiculoId;

    private String sintomas;

    @NotBlank(message = "tagChave é obrigatória")
    private String tagChave;

    @NotEmpty
    private List<@NotNull @Positive Long> servicosDesejados;

    public OrdemDeServico toDomain() {
        return OrdemDeServico.builder()
                .cliente(Cliente.builder().id(this.clienteId).build())
                .veiculo(Veiculo.builder().id(this.veiculoId).build())
                .tagChave(this.tagChave)
                .sintomas(this.sintomas != null && !this.sintomas.isBlank() ? this.sintomas : null)
                .servicosDesejados(getServicosDesejadosDomain())
                .build();
    }

    private List<ServicoOS> getServicosDesejadosDomain() {
        return this.servicosDesejados.stream().map(servicoId -> ServicoOS.builder()
                .servico(Servico.builder().id(servicoId)
                        .build())
                .build()).toList();
    }
}