package com.grupo52.tech_challenge.dto.response;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.dto.EnderecoInfoDTO;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteInfoResponseDTO {

    private String nome;
    private String tipoDocumento;
    private String documento;
    private String email;
    private String telefone;
    private Boolean contatoWhatsApp;
    private EnderecoInfoDTO endereco;

//    private List<VeiculoInfoResponseDTO> veiculos = new ArrayList<>();
//
//    private List<OrdemDeServicoInfoResponseDTO> ordensDeServico = new ArrayList<>();

    public static ClienteInfoResponseDTO fromDomain(Cliente cliente) {
        return ClienteInfoResponseDTO.builder()
                .nome(cliente.getNome())
                .tipoDocumento(cliente.getTipoDocumento().name()) // ou .toString()
                .documento(cliente.getDocumento())
                .email(cliente.getEmail())
                .telefone(cliente.getTelefone())
                .contatoWhatsApp(cliente.getContatoWhatsApp())
                .endereco(EnderecoInfoDTO.fromDomain(cliente.getEndereco()))
//                .veiculos()
//                .ordensDeServico()
                .build();
    }
}
