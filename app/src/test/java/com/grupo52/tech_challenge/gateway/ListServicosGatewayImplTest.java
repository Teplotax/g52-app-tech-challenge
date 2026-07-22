package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ServicoRepository;
import com.grupo52.tech_challenge.gateway.impl.ListServicosGatewayImpl;
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
class ListServicosGatewayImplTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private ListServicosGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        ServicoDatabase servicoDatabase = ServicoDatabase.builder().id(1L).nome("Troca de óleo").build();

        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(servicoDatabase)));

        Page<Servico> result = gateway.execute(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        verify(repository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        when(repository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(Pageable.unpaged()));
    }
}