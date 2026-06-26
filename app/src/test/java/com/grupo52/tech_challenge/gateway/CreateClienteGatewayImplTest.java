package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.EnderecoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import com.grupo52.tech_challenge.gateway.impl.CreateClienteGatewayImpl;
import org.junit.jupiter.api.BeforeEach;
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
public class CreateClienteGatewayImplTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private CreateClienteGatewayImpl gateway;

    private Cliente cliente;
    private ClienteDatabase clienteDatabase;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .nomeSocial("João Silva")
                .documento("12345678909")
                .email("joao@email.com")
                .build();

        clienteDatabase = ClienteDatabase.builder()
                .id(1L)
                .nomeSocial("João Silva")
                .documento("12345678909")
                .email("joao@email.com")
                .endereco(EnderecoDatabase.builder()
                        .logradouro("Rua das Flores")
                        .numero("123")
                        .bairro("Centro")
                        .cidade("São Paulo")
                        .uf("SP")
                        .cep("01310100")
                        .build())
                .build();
    }

    @Test
    void executeSucesso() throws GatewayException {
        when(repository.save(any(ClienteDatabase.class))).thenReturn(clienteDatabase);

        Cliente result = gateway.execute(cliente);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("João Silva", result.getNomeSocial());
        verify(repository, times(1)).save(any(ClienteDatabase.class));
    }

    @Test
    void executeDocumentoDuplicadoLancaGatewayException409() {
        when(repository.save(any(ClienteDatabase.class))).thenThrow(DataIntegrityViolationException.class);

        GatewayException ex = assertThrows(GatewayException.class, () -> gateway.execute(cliente));

        assertEquals(409, ex.getStatus());
        verify(repository, times(1)).save(any(ClienteDatabase.class));
    }

    @Test
    void executeErroGenericoLancaGatewayException() {
        when(repository.save(any(ClienteDatabase.class))).thenThrow(new RuntimeException("db error"));

        assertThrows(GatewayException.class, () -> gateway.execute(cliente));

        verify(repository, times(1)).save(any(ClienteDatabase.class));
    }
}