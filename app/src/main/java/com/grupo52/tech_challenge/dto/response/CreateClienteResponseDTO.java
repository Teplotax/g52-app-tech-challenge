package com.grupo52.tech_challenge.dto.response;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.dto.EnderecoDTO;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateClienteResponseDTO {

    private String nome;
    private String tipoDocumento;
    private String documento;
    private String email;
    private String telefone;
    private Boolean contatoWhatsApp;
    private EnderecoDTO endereco;

    public static CreateClienteResponseDTO fromDomain(Cliente cliente) {
        return CreateClienteResponseDTO.builder()
                .nome(cliente.getNome())
                .tipoDocumento(cliente.getTipoDocumento().name()) // ou .toString()
                .documento(cliente.getDocumento())
                .email(cliente.getEmail())
                .telefone(cliente.getTelefone())
                .contatoWhatsApp(cliente.getContatoWhatsApp())
                .endereco(EnderecoDTO.fromDomain(cliente.getEndereco()))
                .build();
    }
}
