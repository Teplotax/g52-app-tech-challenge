package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import com.grupo52.tech_challenge.gateway.impl.FindInsumoGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindInsumoGatewayImplTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private FindInsumoGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        ProdutoDatabase insumoDatabase = ProdutoDatabase.builder()
                .id(1L).sku("SKU-002").ean("7891234560010").nome("Fluido de freio DOT 4")
                .preco(new BigDecimal("25.00")).estoque(5).estoqueReservado(0).estoqueMinimo(1)
                .tipoProduto(TipoProduto.INSUMO).tipoInsumo(TipoInsumo.FLUIDO_FREIO).aplicacoes(new ArrayList<>())
                .build();

        when(repository.findByIdAndTipoProduto(1L, TipoProduto.INSUMO)).thenReturn(Optional.of(insumoDatabase));

        Insumo result = gateway.execute(1L);

        assertNotNull(result);
        assertEquals("SKU-002", result.getSku());
        verify(repository, times(1)).findByIdAndTipoProduto(1L, TipoProduto.INSUMO);
    }

    @Test
    void executeNaoEncontradoLancaNotFoundGatewayException() {
        when(repository.findByIdAndTipoProduto(1L, TipoProduto.INSUMO)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(1L));
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        when(repository.findByIdAndTipoProduto(1L, TipoProduto.INSUMO)).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(1L));
    }
}