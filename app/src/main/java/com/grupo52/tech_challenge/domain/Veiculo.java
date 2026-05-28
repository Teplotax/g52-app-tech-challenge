package com.grupo52.tech_challenge.domain;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Veiculo {

    private Long id;

    private String placa;

    //OneToOne
    private Marca marca;

    //OneToOne
    private Modelo modelo;

    private Integer ano;

    //OneToOne
    private Cliente cliente;

}
