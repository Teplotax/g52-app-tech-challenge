package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Enums.TipoDocumento;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.EnderecoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import com.grupo52.tech_challenge.gateway.impl.CreateClienteGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateClienteGatewayImplTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private CreateClienteGatewayImpl gateway;

    private ClienteDatabase clienteSalvo() {
        return ClienteDatabase.builder()
                .id(1L)
                .nomeSocial("João Silva")
                .nome("João da Silva")
                .tipoDocumento(TipoDocumento.CPF)
                .documento("123.456.789-00")
                .email("joao.silva@email.com")
                .telefone("11999990000")
                .contatoWhatsApp(true)
                .endereco(EnderecoDatabase.builder().cidade("São Paulo").uf("SP").build())
                .build();
    }

    @Test
    void executeSucesso() throws GatewayException {
        Cliente cliente = Cliente.builder().nomeSocial("João Silva").documento("123.456.789-00").build();

        when(repository.save(any(ClienteDatabase.class))).thenReturn(clienteSalvo());

        Cliente result = gateway.execute(cliente);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("123.456.789-00", result.getDocumento());
        verify(repository, times(1)).save(any(ClienteDatabase.class));
    }

    @Test
    void executeDocumentoDuplicadoLancaGatewayException() {
        Cliente cliente = Cliente.builder().documento("123.456.789-00").build();

        when(repository.save(any(ClienteDatabase.class))).thenThrow(new DataIntegrityViolationException("duplicate key"));

        GatewayException exception = assertThrows(GatewayException.class, () -> gateway.execute(cliente));

        assertEquals(409, exception.getStatus());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        Cliente cliente = Cliente.builder().documento("123.456.789-00").build();

        when(repository.save(any(ClienteDatabase.class))).thenThrow(new RuntimeException("erro inesperado"));

        assertThrows(GatewayException.class, () -> gateway.execute(cliente));
    }
}