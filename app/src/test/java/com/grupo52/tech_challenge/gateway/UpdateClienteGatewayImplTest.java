package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Endereco;
import com.grupo52.tech_challenge.domain.Enums.TipoDocumento;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.EnderecoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import com.grupo52.tech_challenge.gateway.impl.UpdateClienteGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateClienteGatewayImplTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private UpdateClienteGatewayImpl gateway;

    private ClienteDatabase existingClienteDatabase() {
        return ClienteDatabase.builder()
                .id(1L)
                .nomeSocial("João Silva")
                .nome("João da Silva")
                .tipoDocumento(TipoDocumento.CPF)
                .documento("123.456.789-00")
                .email("joao.silva@email.com")
                .telefone("11999990000")
                .contatoWhatsApp(true)
                .endereco(EnderecoDatabase.builder().logradouro("Rua A").cidade("São Paulo").uf("SP").build())
                .build();
    }

    @Test
    void executeSucesso() throws GatewayException {
        Cliente cliente = Cliente.builder().id(1L).telefone("11988887777").build();

        when(repository.findById(1L)).thenReturn(Optional.of(existingClienteDatabase()));
        when(repository.save(any(ClienteDatabase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente result = gateway.execute(cliente);

        assertEquals("11988887777", result.getTelefone());
        assertEquals("João Silva", result.getNomeSocial());
        assertEquals("123.456.789-00", result.getDocumento());
        verify(repository, times(1)).save(any(ClienteDatabase.class));
    }

    @Test
    void executeMantemEnderecoExistenteQuandoNaoInformado() throws GatewayException {
        Cliente cliente = Cliente.builder().id(1L).build();

        when(repository.findById(1L)).thenReturn(Optional.of(existingClienteDatabase()));
        when(repository.save(any(ClienteDatabase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente result = gateway.execute(cliente);

        assertEquals("Rua A", result.getEndereco().getLogradouro());
    }

    @Test
    void executeAtualizaEnderecoParcialmente() throws GatewayException {
        Cliente cliente = Cliente.builder().id(1L)
                .endereco(Endereco.builder().cidade("Campinas").build())
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(existingClienteDatabase()));
        when(repository.save(any(ClienteDatabase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente result = gateway.execute(cliente);

        assertEquals("Campinas", result.getEndereco().getCidade());
        assertEquals("Rua A", result.getEndereco().getLogradouro());
    }

    @Test
    void executeNaoEncontradoLancaNotFoundGatewayException() {
        Cliente cliente = Cliente.builder().id(1L).build();

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(cliente));

        verify(repository, never()).save(any());
    }

    @Test
    void executeDocumentoDuplicadoLancaGatewayException() {
        Cliente cliente = Cliente.builder().id(1L).documento("999.999.999-99").build();

        when(repository.findById(1L)).thenReturn(Optional.of(existingClienteDatabase()));
        when(repository.save(any(ClienteDatabase.class))).thenThrow(new DataIntegrityViolationException("duplicate key"));

        GatewayException exception = assertThrows(GatewayException.class, () -> gateway.execute(cliente));

        assertEquals(409, exception.getStatus());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        Cliente cliente = Cliente.builder().id(1L).build();

        when(repository.findById(1L)).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(cliente));
    }
}