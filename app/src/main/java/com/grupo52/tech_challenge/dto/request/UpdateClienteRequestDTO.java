package com.grupo52.tech_challenge.dto.request;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Endereco;
import com.grupo52.tech_challenge.domain.Enums.TipoDocumento;
import com.grupo52.tech_challenge.validation.annotation.DocumentoBrasilValido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DocumentoBrasilValido
public class UpdateClienteRequestDTO {

    private String nome;

    @Pattern(regexp = "CPF|CNPJ", message = "deve ser 'CPF' ou 'CNPJ'")
    private String tipoDocumento;

    @Pattern(regexp = "^\\d{11,14}$", message = "Deve conter 11 ou 14 dígitos numéricos, sem caracteres especiais")
    private String documento;

    @Email
    private String email;

    @Pattern(
            regexp = "^[0-9]{11}$",
            message = "deve conter exatamente 11 dígitos numéricos (ex: 11999999999)"
    )
    private String telefone;

    private Boolean contatoWhatsApp;

    @Valid
    private UpdateEnderecoDTO endereco;

    public Cliente toDomain(Long clienteId) {
        return Cliente.builder()
                .id(clienteId)
                .nome(this.nome)
                .tipoDocumento(this.tipoDocumento != null ? TipoDocumento.valueOf(this.tipoDocumento) : null)
                .documento(this.documento)
                .email(this.email)
                .telefone(this.telefone)
                .contatoWhatsApp(this.contatoWhatsApp)
                .endereco(this.endereco != null ? this.endereco.toDomain() : null)
                .build();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UpdateEnderecoDTO {

        private String logradouro;

        private String numero;

        private String complemento;

        private String bairro;

        private String cidade;

        @Pattern(regexp = "[A-Z]{2}", message = "UF deve conter 2 letras maiúsculas")
        private String uf;

        @Pattern(regexp = "\\d{8}", message = "CEP deve conter exatamente 8 dígitos numéricos (ex: 12345678)")
        private String cep;

        public static UpdateEnderecoDTO fromDomain(Endereco endereco) {
            if (endereco == null) return null;
            return UpdateEnderecoDTO.builder()
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
}
