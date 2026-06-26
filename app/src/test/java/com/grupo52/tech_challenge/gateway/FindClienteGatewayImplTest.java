package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.EnderecoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import com.grupo52.tech_challenge.gateway.impl.FindClienteGatewayImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FindClienteGatewayImplTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private FindClienteGatewayImpl gateway;

    private ClienteDatabase clienteDatabase;

    @BeforeEach
    void setUp() {
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
        when(repository.findById(1L)).thenReturn(Optional.of(clienteDatabase));

        Cliente result = gateway.execute(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("João Silva", result.getNomeSocial());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void executeNaoEncontradoLancaNotFoundGatewayException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(99L));

        verify(repository, times(1)).findById(99L);
    }

    @Test
    void executeErroGenericoLancaGatewayException() {
        when(repository.findById(1L)).thenThrow(new RuntimeException("db error"));

        assertThrows(GatewayException.class, () -> gateway.execute(1L));

        verify(repository, times(1)).findById(1L);
    }
}