package com.grupo52.tech_challenge.dto.request;

import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.domain.Veiculo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateVeiculoRequestDTO {

    @NotNull
    private Long clienteId;

    @NotNull
    private Long modeloId;

    @NotBlank
    private String placa;

    @NotBlank
    private String cor;

    @Min(1000)
    @Max(9999)
    private Integer ano;


    public Veiculo toDomain() {
        return Veiculo.builder()
                .placa(this.placa)
                .clienteId(this.clienteId)
                .modelo(Modelo.builder().id(modeloId).build())
                .ano(this.ano)
                .cor(this.cor)
                .build();
    }
}