package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Enums.TipoDocumento;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.EnderecoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import com.grupo52.tech_challenge.gateway.impl.ListClientesGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListClientesGatewayImplTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ListClientesGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        ClienteDatabase clienteDatabase = ClienteDatabase.builder()
                .id(1L)
                .nomeSocial("João Silva")
                .tipoDocumento(TipoDocumento.CPF)
                .documento("123.456.789-00")
                .endereco(EnderecoDatabase.builder().cidade("São Paulo").uf("SP").build())
                .build();

        when(repository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(clienteDatabase)));

        Page<Cliente> result = gateway.execute(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals("123.456.789-00", result.getContent().get(0).getDocumento());
        verify(repository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        when(repository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(Pageable.unpaged()));
    }
}