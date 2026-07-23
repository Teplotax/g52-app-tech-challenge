package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import com.grupo52.tech_challenge.gateway.impl.FindInsumoByVeiculoGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindInsumoByVeiculoGatewayImplTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private FindInsumoByVeiculoGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        Veiculo veiculo = Veiculo.builder().modelo(Modelo.builder().id(1L).build()).ano(2020).build();

        ProdutoDatabase insumoDatabase = ProdutoDatabase.builder()
                .id(1L).sku("SKU-002").ean("7891234560010").nome("Fluido de freio DOT 4")
                .preco(new BigDecimal("25.00")).estoque(5).estoqueReservado(0).estoqueMinimo(1)
                .tipoProduto(TipoProduto.INSUMO).tipoInsumo(TipoInsumo.FLUIDO_FREIO).aplicacoes(new ArrayList<>())
                .build();

        when(repository.findAllByTipoInsumoAndAplicacoesModeloIdAndAplicacoesAnoInicioLessThanEqualAndAplicacoesAnoFimGreaterThanEqual(
                TipoInsumo.FLUIDO_FREIO, 1L, 2020, 2020))
                .thenReturn(List.of(insumoDatabase));

        List<Insumo> result = gateway.execute(TipoInsumo.FLUIDO_FREIO, veiculo);

        assertEquals(1, result.size());
        assertEquals("SKU-002", result.get(0).getSku());
    }

    @Test
    void executeSemResultadosRetornaListaVazia() throws GatewayException {
        Veiculo veiculo = Veiculo.builder().modelo(Modelo.builder().id(1L).build()).ano(2020).build();

        when(repository.findAllByTipoInsumoAndAplicacoesModeloIdAndAplicacoesAnoInicioLessThanEqualAndAplicacoesAnoFimGreaterThanEqual(
                TipoInsumo.FLUIDO_FREIO, 1L, 2020, 2020))
                .thenReturn(List.of());

        List<Insumo> result = gateway.execute(TipoInsumo.FLUIDO_FREIO, veiculo);

        assertTrue(result.isEmpty());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        Veiculo veiculo = Veiculo.builder().modelo(Modelo.builder().id(1L).build()).ano(2020).build();

        when(repository.findAllByTipoInsumoAndAplicacoesModeloIdAndAplicacoesAnoInicioLessThanEqualAndAplicacoesAnoFimGreaterThanEqual(
                TipoInsumo.FLUIDO_FREIO, 1L, 2020, 2020))
                .thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(TipoInsumo.FLUIDO_FREIO, veiculo));
    }
}