package com.grupo52.tech_challenge.dto;

import com.grupo52.tech_challenge.domain.Endereco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnderecoInfoDTO {

    @NotBlank(message = "Logradouro é obrigatório")
    private String logradouro;

    @NotBlank(message = "Número é obrigatório")
    private String numero;

    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "UF é obrigatória")
    @Pattern(regexp = "[A-Z]{2}", message = "UF deve conter 2 letras maiúsculas")
    private String uf;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter exatamente 8 dígitos numéricos (ex: 12345678)")
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