package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.EnderecoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.MarcaDatabase;
import com.grupo52.tech_challenge.gateway.database.model.ModeloDatabase;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.OrdemDeServicoRepository;
import com.grupo52.tech_challenge.gateway.impl.FindOrdemGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindOrdemGatewayImplTest {

    @Mock
    private OrdemDeServicoRepository repository;

    @InjectMocks
    private FindOrdemGatewayImpl gateway;

    private OrdemDeServicoDatabase osDatabase() {
        MarcaDatabase marca = MarcaDatabase.builder().id(1L).nome("Toyota").build();
        ModeloDatabase modelo = ModeloDatabase.builder().id(1L).nome("Corolla").marca(marca).build();
        VeiculoDatabase veiculo = VeiculoDatabase.builder()
                .id(1L).placa("ABC1D23").ano(2020).cor("Prata")
                .modelo(modelo).cliente(ClienteDatabase.builder().id(1L).endereco(EnderecoDatabase.builder().build()).build())
                .build();

        return OrdemDeServicoDatabase.builder()
                .id(1L).status(Status.EM_DIAGNOSTICO)
                .cliente(veiculo.getCliente()).veiculo(veiculo)
                .servicosDesejados(new ArrayList<>())
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .historico(new ArrayList<>())
                .build();
    }

    @Test
    void executeSucesso() throws GatewayException {
        when(repository.findById(1L)).thenReturn(Optional.of(osDatabase()));

        Ordem result = gateway.execute(1L);

        assertNotNull(result);
        assertEquals(Status.EM_DIAGNOSTICO, result.getStatus());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void executeNaoEncontradaLancaNotFoundGatewayException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(1L));
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        when(repository.findById(1L)).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(1L));
    }
}