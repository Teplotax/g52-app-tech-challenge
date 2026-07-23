package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Enums.TipoDocumento;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.EnderecoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import com.grupo52.tech_challenge.gateway.impl.FindClienteGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindClienteGatewayImplTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private FindClienteGatewayImpl gateway;

    private ClienteDatabase clienteDatabase() {
        return ClienteDatabase.builder()
                .id(1L)
                .nomeSocial("João Silva")
                .tipoDocumento(TipoDocumento.CPF)
                .documento("123.456.789-00")
                .endereco(EnderecoDatabase.builder().cidade("São Paulo").uf("SP").build())
                .build();
    }

    @Test
    void executeSucesso() throws GatewayException {
        when(repository.findById(1L)).thenReturn(Optional.of(clienteDatabase()));

        Cliente result = gateway.execute(1L);

        assertNotNull(result);
        assertEquals("123.456.789-00", result.getDocumento());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void executeNaoEncontradoLancaNotFoundGatewayException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(1L));
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        when(repository.findById(1L)).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(1L));
    }
}