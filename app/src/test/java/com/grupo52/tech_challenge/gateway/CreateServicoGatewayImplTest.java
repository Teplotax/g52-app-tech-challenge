package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ServicoRepository;
import com.grupo52.tech_challenge.gateway.impl.CreateServicoGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateServicoGatewayImplTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private CreateServicoGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        Servico servico = Servico.builder().nome("Troca de óleo").horasTecnicas(new BigDecimal("1.5")).build();
        ServicoDatabase salvo = ServicoDatabase.builder().id(1L).nome("Troca de óleo").horasTecnicas(new BigDecimal("1.5")).build();

        when(repository.save(any(ServicoDatabase.class))).thenReturn(salvo);

        Servico result = gateway.execute(servico);

        assertNotNull(result);
        assertEquals("Troca de óleo", result.getNome());
        verify(repository, times(1)).save(any(ServicoDatabase.class));
    }

    @Test
    void executeNomeDuplicadoLancaGatewayException() {
        Servico servico = Servico.builder().nome("Troca de óleo").build();

        when(repository.save(any(ServicoDatabase.class))).thenThrow(new DataIntegrityViolationException("duplicate key"));

        GatewayException exception = assertThrows(GatewayException.class, () -> gateway.execute(servico));

        assertEquals(409, exception.getStatus());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        Servico servico = Servico.builder().nome("Troca de óleo").build();

        when(repository.save(any(ServicoDatabase.class))).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(servico));
    }
}