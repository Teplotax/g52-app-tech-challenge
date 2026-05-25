package com.grupo52.tech_challenge.dto.request;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Enums.TipoDocumento;
import com.grupo52.tech_challenge.dto.EnderecoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateClienteRequestDTO {

    @NotBlank
    private String nome;

    @NotBlank
    @Pattern(regexp = "CPF|CNPJ", message = "deve ser 'CPF' ou 'CNPJ'")
    private String tipoDocumento;

    @NotBlank
    private String documento;

    @NotBlank
    @Email()
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
    private EnderecoDTO endereco;

    public Cliente toDomain() {

        System.out.println(this.tipoDocumento);

        return Cliente.builder()
                .nome(this.nome)
                .tipoDocumento(TipoDocumento.valueOf(this.tipoDocumento))
                .documento(this.documento)
                .email(this.email)
                .telefone(this.telefone)
                .contatoWhatsApp(this.contatoWhatsApp)
                .endereco(this.endereco.toDomain())
                .build();
    }
}
