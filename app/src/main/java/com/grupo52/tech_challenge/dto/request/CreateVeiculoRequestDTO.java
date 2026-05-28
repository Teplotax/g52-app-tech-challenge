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
public class CreateVeiculoRequestDTO {

    private Long clienteId;

    private Long modeloId;

    private String placa;

    private Integer ano;


    public Veiculo toDomain() {
        return Veiculo.builder()
                .placa(this.placa)
                .cliente(Cliente.builder().id(this.clienteId).build())
                .modelo(Modelo.builder().id(modeloId).build())
                .ano(this.ano)
                .build();
    }
}