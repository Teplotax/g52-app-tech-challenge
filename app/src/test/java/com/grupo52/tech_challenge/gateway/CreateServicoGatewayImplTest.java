package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ServicoRepository;
import com.grupo52.tech_challenge.gateway.impl.CreateServicoGatewayImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateServicoGatewayImplTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private CreateServicoGatewayImpl gateway;

    private Servico servico;
    private ServicoDatabase servicoDatabase;

    @BeforeEach
    void setUp() {
        servico = Servico.builder()
                .nome("Revisão de freios")
                .horasTecnicas(new BigDecimal("2.0"))
                .insumos(new ArrayList<>())
                .pecas(new ArrayList<>())
                .build();

        servicoDatabase = ServicoDatabase.builder()
                .id(1L)
                .nome("Revisão de freios")
                .horasTecnicas(new BigDecimal("2.0"))
                .build();
    }

    @Test
    void executeSucesso() throws GatewayException {
        when(repository.save(any(ServicoDatabase.class))).thenReturn(servicoDatabase);

        Servico result = gateway.execute(servico);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Revisão de freios", result.getNome());
        verify(repository, times(1)).save(any(ServicoDatabase.class));
    }

    @Test
    void executeNomeDuplicadoLancaGatewayException409() {
        when(repository.save(any(ServicoDatabase.class))).thenThrow(DataIntegrityViolationException.class);

        GatewayException ex = assertThrows(GatewayException.class, () -> gateway.execute(servico));

        assertEquals(409, ex.getStatus());
        verify(repository, times(1)).save(any(ServicoDatabase.class));
    }

    @Test
    void executeErroGenericoLancaGatewayException() {
        when(repository.save(any(ServicoDatabase.class))).thenThrow(new RuntimeException("db error"));

        assertThrows(GatewayException.class, () -> gateway.execute(servico));

        verify(repository, times(1)).save(any(ServicoDatabase.class));
    }
}