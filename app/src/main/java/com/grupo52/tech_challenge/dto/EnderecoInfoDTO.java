package com.grupo52.tech_challenge.dto;

import com.grupo52.tech_challenge.domain.Endereco;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnderecoInfoDTO {

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    private String uf;

    private String cep;

    public static EnderecoInfoDTO fromDomain(Endereco endereco) {
        if (endereco == null) return null;
        return EnderecoInfoDTO.builder()
                .logradouro(endereco.getLogradouro())
                .numero(endereco.getNumero())
                .complemento(endereco.getComplemento())
                .bairro(endereco.getBairro())
                .cidade(endereco.getCidade())
                .uf(endereco.getUf())
                .cep(endereco.getCep())
                .build();
    }

    public Endereco toDomain() {
        return Endereco.builder()
                .logradouro(this.logradouro)
                .numero(this.numero)
                .complemento(this.complemento)
                .bairro(this.bairro)
                .cidade(this.cidade)
                .uf(this.uf)
                .cep(this.cep)
                .build();
    }
}