package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.UpdateClienteGateway;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.EnderecoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateClienteGatewayImpl implements UpdateClienteGateway {

    private final ClienteRepository repository;

    @Override
    public Cliente execute(Cliente cliente) throws GatewayException {
        try {
            Optional<ClienteDatabase> clienteOptional = repository.findById(cliente.getId());
            ClienteDatabase existing = clienteOptional.orElseThrow(
                    () -> new NotFoundGatewayException("Cliente não encontrado")
            );

            ClienteDatabase updated = ClienteDatabase.builder()
                    .id(existing.getId())
                    .nomeSocial(cliente.getNomeSocial() != null ? cliente.getNomeSocial() : existing.getNomeSocial())
                    .nome(cliente.getNome() != null ? cliente.getNome() : existing.getNome())
                    .tipoDocumento(cliente.getTipoDocumento() != null ? cliente.getTipoDocumento() : existing.getTipoDocumento())
                    .documento(cliente.getDocumento() != null ? cliente.getDocumento() : existing.getDocumento())
                    .email(cliente.getEmail() != null ? cliente.getEmail() : existing.getEmail())
                    .telefone(cliente.getTelefone() != null ? cliente.getTelefone() : existing.getTelefone())
                    .contatoWhatsApp(cliente.getContatoWhatsApp() != null ? cliente.getContatoWhatsApp() : existing.getContatoWhatsApp())
                    .endereco(buildEndereco(cliente, existing))
                    .build();

            return repository.save(updated).toDomain();
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new GatewayException("Falha ao atualizar Cliente, documento já cadastrado", 409);
        } catch (Exception e) {
            throw new GatewayException("Falha ao atualizar Cliente, cause: " + e.getClass().getSimpleName(), e);
        }
    }

    private EnderecoDatabase buildEndereco(Cliente cliente, ClienteDatabase existing) {
        if (cliente.getEndereco() == null) return existing.getEndereco();

        var novo = cliente.getEndereco();
        var atual = existing.getEndereco();

        return EnderecoDatabase.builder()
                .logradouro(novo.getLogradouro() != null ? novo.getLogradouro() : (atual != null ? atual.getLogradouro() : null))
                .numero(novo.getNumero() != null ? novo.getNumero() : (atual != null ? atual.getNumero() : null))
                .complemento(novo.getComplemento() != null ? novo.getComplemento() : (atual != null ? atual.getComplemento() : null))
                .bairro(novo.getBairro() != null ? novo.getBairro() : (atual != null ? atual.getBairro() : null))
                .cidade(novo.getCidade() != null ? novo.getCidade() : (atual != null ? atual.getCidade() : null))
                .uf(novo.getUf() != null ? novo.getUf() : (atual != null ? atual.getUf() : null))
                .cep(novo.getCep() != null ? novo.getCep() : (atual != null ? atual.getCep() : null))
                .build();
    }
}