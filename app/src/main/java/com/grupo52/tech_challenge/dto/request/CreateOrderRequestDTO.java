package com.grupo52.tech_challenge.dto.request;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.domain.OrdemServico;
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
public class CreateOrderRequestDTO {

    @NotBlank
    @Placa
    private String placa;

    private String sintomas;

    @NotBlank(message = "tagChave é obrigatória")
    private String tagChave;

    @NotEmpty
    private List<@NotNull @Positive Long> servicosDesejados;

    public Ordem toDomain() {
        return Ordem.builder()
                .veiculo(Veiculo.builder().placa(this.placa).build())
                .tagChave(this.tagChave)
                .sintomas(this.sintomas != null && !this.sintomas.isBlank() ? this.sintomas : null)
                .status(Status.RECEBIDA)
                .servicosDesejados(getServicosDesejadosDomain())
                .build();
    }

    private List<OrdemServico> getServicosDesejadosDomain() {
        return this.servicosDesejados.stream().map(servicoId -> OrdemServico.builder()
                .servico(Servico.builder().id(servicoId)
                        .build())
                .aprovado(false)
                .build()).toList();
    }
}