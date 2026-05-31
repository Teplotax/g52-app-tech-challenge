package com.grupo52.tech_challenge.dto.response;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.dto.EnderecoInfoDTO;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindClienteResponseDTO {


    private String nome;
    private String tipoDocumento;
    private String documento;
    private String email;
    private String telefone;
    private Boolean contatoWhatsApp;
    private EnderecoInfoDTO endereco;

    public static FindClienteResponseDTO fromDomain(Cliente cliente) {
        return FindClienteResponseDTO.builder()
                .nome(cliente.getNome())
                .tipoDocumento(cliente.getTipoDocumento().name()) // ou .toString()
                .documento(cliente.getDocumento())
                .email(cliente.getEmail())
                .telefone(cliente.getTelefone())
                .contatoWhatsApp(cliente.getContatoWhatsApp())
                .endereco(EnderecoInfoDTO.fromDomain(cliente.getEndereco()))
                .build();
    }
}
