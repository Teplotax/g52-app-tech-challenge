package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ServicoRepository;
import com.grupo52.tech_challenge.gateway.impl.FindServicoGatewayImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FindServicoGatewayImplTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private FindServicoGatewayImpl gateway;

    private ServicoDatabase servicoDatabase;

    @BeforeEach
    void setUp() {
        servicoDatabase = ServicoDatabase.builder()
                .id(1L)
                .nome("Revisão de freios")
                .horasTecnicas(new BigDecimal("2.0"))
                .build();
    }

    @Test
    void executeSucesso() throws GatewayException {
        when(repository.findById(1L)).thenReturn(Optional.of(servicoDatabase));

        Servico result = gateway.execute(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Revisão de freios", result.getNome());
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