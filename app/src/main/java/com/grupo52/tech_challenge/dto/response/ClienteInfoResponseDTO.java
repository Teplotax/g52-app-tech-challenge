package com.grupo52.tech_challenge.dto.response;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.dto.EnderecoInfoDTO;
import com.grupo52.tech_challenge.dto.PagedResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteInfoResponseDTO {

    private Long id;
    private String nome;
    private String tipoDocumento;
    private String documento;
    private String email;
    private String telefone;
    private Boolean contatoWhatsApp;
    private EnderecoInfoDTO endereco;

    public static ClienteInfoResponseDTO fromDomain(Cliente cliente) {
        return ClienteInfoResponseDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .tipoDocumento(cliente.getTipoDocumento().name()) // ou .toString()
                .documento(cliente.getDocumento())
                .email(cliente.getEmail())
                .telefone(cliente.getTelefone())
                .contatoWhatsApp(cliente.getContatoWhatsApp())
                .endereco(EnderecoInfoDTO.fromDomain(cliente.getEndereco()))
                .build();
    }

    public static List<ClienteInfoResponseDTO> fromDomain(List<Cliente> clientes) {
        if (clientes == null) return List.of();
        return clientes.stream()
                .map(ClienteInfoResponseDTO::fromDomain)
                .toList();
    }

    public static PagedResponse<ClienteInfoResponseDTO> fromDomain(Page<Cliente> clientes) {
        return PagedResponse.<ClienteInfoResponseDTO>builder()
                .content(clientes.getContent().stream()
                        .map(ClienteInfoResponseDTO::fromDomain)
                        .toList())
                .page(clientes.getNumber())
                .size(clientes.getSize())
                .totalElements(clientes.getTotalElements())
                .totalPages(clientes.getTotalPages())
                .build();
    }
}
