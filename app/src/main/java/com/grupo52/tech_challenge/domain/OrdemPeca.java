package com.grupo52.tech_challenge.domain;

import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrdemPeca {

    private Peca peca;

    private Integer quantidade;

    private BigDecimal precoTotal;

    @Setter
    @Builder.Default
    private Boolean reservado = false;
}