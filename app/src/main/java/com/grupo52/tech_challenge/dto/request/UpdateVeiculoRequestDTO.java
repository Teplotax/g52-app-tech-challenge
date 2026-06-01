package com.grupo52.tech_challenge.dto.request;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.domain.Veiculo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVeiculoRequestDTO {

    private Long clienteId;

    private Long modeloId;

    private String placa;

    private String cor;

    private Integer ano;


    public Veiculo toDomain(Long veiculoId) {
        return Veiculo.builder()
                .id(veiculoId)
                .placa(this.placa)
                .cliente(this.clienteId != null ? Cliente.builder().id(this.clienteId).build() : null)
                .modelo(this.modeloId != null ? Modelo.builder().id(this.modeloId).build() : null)
                .ano(this.ano)
                .cor(this.cor)
                .build();
    }
}