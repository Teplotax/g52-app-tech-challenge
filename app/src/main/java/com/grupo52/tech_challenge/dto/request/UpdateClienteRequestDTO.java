package com.grupo52.tech_challenge.dto.request;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Enums.TipoDocumento;
import com.grupo52.tech_challenge.dto.EnderecoInfoDTO;
import com.grupo52.tech_challenge.validation.annotation.DocumentoBrasilValido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DocumentoBrasilValido
public class UpdateClienteRequestDTO {

    private String nome;

    @Pattern(regexp = "CPF|CNPJ", message = "deve ser 'CPF' ou 'CNPJ'")
    private String tipoDocumento;

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
    private EnderecoInfoDTO endereco;

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
}
