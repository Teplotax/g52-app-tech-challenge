package com.grupo52.tech_challenge.domain;

import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AplicacaoProduto {
    private Long id;
    private Produto produto;
    private Modelo modelo;
    @Builder.Default
    private Integer quantidade = 1;
    private Integer anoInicio;
    private Integer anoFim;
}
