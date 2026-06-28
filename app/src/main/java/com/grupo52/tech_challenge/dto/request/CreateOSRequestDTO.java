package com.grupo52.tech_challenge.dto.request;

import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.domain.ServicoOS;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.validation.annotation.Placa;
import com.grupo52.tech_challenge.validation.annotation.SafeDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@SafeDto
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOSRequestDTO {

    @NotBlank
    @Placa
    private String placa;

    private String sintomas;

    @NotBlank(message = "tagChave é obrigatória")
    private String tagChave;

    @NotEmpty
    private List<@NotNull @Positive Long> servicosDesejados;

    public OrdemDeServico toDomain() {
        return OrdemDeServico.builder()
                .veiculo(Veiculo.builder().placa(this.placa).build())
                .tagChave(this.tagChave)
                .sintomas(this.sintomas != null && !this.sintomas.isBlank() ? this.sintomas : null)
                .servicosDesejados(getServicosDesejadosDomain())
                .build();
    }

    private List<ServicoOS> getServicosDesejadosDomain() {
        return this.servicosDesejados.stream().map(servicoId -> ServicoOS.builder()
                .servico(Servico.builder().id(servicoId)
                        .build())
                .aprovado(false)
                .build()).toList();
    }
}