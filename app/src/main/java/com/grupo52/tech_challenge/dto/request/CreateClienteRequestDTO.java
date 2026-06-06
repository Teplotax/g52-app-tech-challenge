package com.grupo52.tech_challenge.dto.request;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Endereco;
import com.grupo52.tech_challenge.domain.Enums.TipoDocumento;
import com.grupo52.tech_challenge.validation.annotation.DocumentoBrasil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DocumentoBrasil
public class CreateClienteRequestDTO {

    private String nomeSocial;

    @NotBlank
    private String nome;

    @NotBlank
    @Pattern(regexp = "CPF|CNPJ", message = "deve ser 'CPF' ou 'CNPJ'")
    private String tipoDocumento;

    @NotBlank
    @Pattern(regexp = "^\\d{11,14}$", message = "deve conter 11 ou 14 dígitos numéricos, sem caracteres especiais")
    private String documento;

    @NotBlank
    @Email(message = "deve ser um email válido")
    private String email;

    @NotBlank
    @Pattern(
            regexp = "^[0-9]{11}$",
            message = "deve conter exatamente 11 dígitos numéricos (ex: 11999999999)"
    )
    private String telefone;

    @NotNull
    private Boolean contatoWhatsApp;

    @NotNull
    @Valid
    private CreateEnderecoDTO endereco;

    public Cliente toDomain() {
        return Cliente.builder()
                .nomeSocial(this.nomeSocial != null && !this.nomeSocial.isBlank() ? this.nomeSocial : this.nome)
                .nome(this.nome)
                .tipoDocumento(TipoDocumento.valueOf(this.tipoDocumento))
                .documento(this.documento)
                .email(this.email)
                .telefone(this.telefone)
                .contatoWhatsApp(this.contatoWhatsApp)
                .endereco(this.endereco.toDomain())
                .build();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateEnderecoDTO {

        @NotBlank(message = "logradouro é obrigatório")
        private String logradouro;

        @NotBlank(message = "número é obrigatório")
        private String numero;

        private String complemento;

        @NotBlank(message = "bairro é obrigatório")
        private String bairro;

        @NotBlank(message = "cidade é obrigatória")
        private String cidade;

        @NotBlank(message = "UF é obrigatória")
        @Pattern(regexp = "[A-Z]{2}", message = "UF deve conter 2 letras maiúsculas")
        private String uf;

        @NotBlank(message = "CEP é obrigatório")
        @Pattern(regexp = "\\d{8}", message = "CEP deve conter exatamente 8 dígitos numéricos (ex: 12345678)")
        private String cep;

        public static CreateEnderecoDTO fromDomain(Endereco endereco) {
            if (endereco == null) return null;
            return CreateEnderecoDTO.builder()
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