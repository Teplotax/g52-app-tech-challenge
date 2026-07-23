package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.EnderecoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.MarcaDatabase;
import com.grupo52.tech_challenge.gateway.database.model.ModeloDatabase;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.OrdemDeServicoRepository;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import com.grupo52.tech_challenge.gateway.impl.CreateOrdemGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrdemGatewayImplTest {

    @Mock
    private OrdemDeServicoRepository ordemDeServicoRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private CreateOrdemGatewayImpl gateway;

    private VeiculoDatabase veiculoDatabase() {
        MarcaDatabase marca = MarcaDatabase.builder().id(1L).nome("Toyota").build();
        ModeloDatabase modelo = ModeloDatabase.builder().id(1L).nome("Corolla").marca(marca).build();
        return VeiculoDatabase.builder()
                .id(1L).placa("ABC1D23").ano(2020).cor("Prata")
                .modelo(modelo).cliente(ClienteDatabase.builder().id(1L).nomeSocial("João Silva")
                        .endereco(EnderecoDatabase.builder().build())
                        .build())
                .build();
    }

    private OrdemDeServicoDatabase osSalva() {
        VeiculoDatabase veiculoDb = veiculoDatabase();
        return OrdemDeServicoDatabase.builder()
                .id(1L).status(Status.RECEBIDA)
                .cliente(veiculoDb.getCliente()).veiculo(veiculoDb)
                .tagChave("001")
                .servicosDesejados(new ArrayList<>())
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .historico(new ArrayList<>())
                .build();
    }

    @Test
    void executeSucesso() throws GatewayException, UseCaseException {
        Ordem os = Ordem.builder()
                .status(Status.RECEBIDA)
                .veiculo(Veiculo.builder().placa("ABC1D23").build())
                .tagChave("001")
                .servicosDesejados(new ArrayList<>())
                .build();

        when(veiculoRepository.findByPlaca("ABC1D23")).thenReturn(Optional.of(veiculoDatabase()));
        when(ordemDeServicoRepository.saveAndFlush(any(OrdemDeServicoDatabase.class))).thenReturn(osSalva());

        Ordem result = gateway.execute(os);

        assertNotNull(result);
        assertEquals(Status.RECEBIDA, result.getStatus());
        assertEquals("001", result.getTagChave());
        verify(ordemDeServicoRepository, times(1)).saveAndFlush(any(OrdemDeServicoDatabase.class));
    }

    @Test
    void executeVeiculoNaoEncontradoLancaNotFoundGatewayException() {
        Ordem os = Ordem.builder().veiculo(Veiculo.builder().placa("ZZZ9Z99").build()).build();

        when(veiculoRepository.findByPlaca("ZZZ9Z99")).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(os));

        verify(ordemDeServicoRepository, never()).saveAndFlush(any());
    }

    @Test
    void executeTagChaveDuplicadaLancaGatewayException() {
        Ordem os = Ordem.builder().veiculo(Veiculo.builder().placa("ABC1D23").build()).tagChave("001").build();

        when(veiculoRepository.findByPlaca("ABC1D23")).thenReturn(Optional.of(veiculoDatabase()));
        when(ordemDeServicoRepository.saveAndFlush(any(OrdemDeServicoDatabase.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint tagChave"));

        GatewayException exception = assertThrows(GatewayException.class, () -> gateway.execute(os));

        assertEquals(409, exception.getStatus());
        assertTrue(exception.getMessage().contains("tagChave"));
    }

    @Test
    void executeViolacaoIntegridadeGenericaLancaGatewayException() {
        Ordem os = Ordem.builder().veiculo(Veiculo.builder().placa("ABC1D23").build()).build();

        when(veiculoRepository.findByPlaca("ABC1D23")).thenReturn(Optional.of(veiculoDatabase()));
        when(ordemDeServicoRepository.saveAndFlush(any(OrdemDeServicoDatabase.class)))
                .thenThrow(new DataIntegrityViolationException("some other constraint violated"));

        GatewayException exception = assertThrows(GatewayException.class, () -> gateway.execute(os));

        assertEquals(409, exception.getStatus());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        Ordem os = Ordem.builder().veiculo(Veiculo.builder().placa("ABC1D23").build()).build();

        when(veiculoRepository.findByPlaca("ABC1D23")).thenReturn(Optional.of(veiculoDatabase()));
        when(ordemDeServicoRepository.saveAndFlush(any(OrdemDeServicoDatabase.class)))
                .thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(os));
    }
}