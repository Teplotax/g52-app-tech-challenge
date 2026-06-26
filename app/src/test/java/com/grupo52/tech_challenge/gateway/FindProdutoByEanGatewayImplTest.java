package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Produto;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import com.grupo52.tech_challenge.gateway.impl.FindProdutoByEanGatewayImpl;
import org.junit.jupiter.api.BeforeEach;
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
public class FindProdutoByEanGatewayImplTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private FindProdutoByEanGatewayImpl gateway;

    private ProdutoDatabase produtoPeca;
    private ProdutoDatabase produtoInsumo;

    @BeforeEach
    void setUp() {
        produtoPeca = ProdutoDatabase.builder()
                .id(1L).sku("SKU-001").ean("7891234560001").nome("Pastilha de freio")
                .preco(new BigDecimal("35.00")).estoque(10).estoqueReservado(0).estoqueMinimo(2)
                .tipoProduto(TipoProduto.PECA).aplicacoes(new ArrayList<>()).build();

        produtoInsumo = ProdutoDatabase.builder()
                .id(2L).sku("SKU-002").ean("7891234560010").nome("Fluido de freio DOT 4")
                .preco(new BigDecimal("25.00")).estoque(5).estoqueReservado(0).estoqueMinimo(1)
                .tipoProduto(TipoProduto.INSUMO).aplicacoes(new ArrayList<>()).build();
    }

    @Test
    void executePecaSucesso() throws GatewayException {
        when(repository.findByEan("7891234560001")).thenReturn(Optional.of(produtoPeca));

        Produto result = gateway.execute("7891234560001");

        assertNotNull(result);
        assertEquals("7891234560001", result.getEan());
        verify(repository, times(1)).findByEan("7891234560001");
    }

    @Test
    void executeInsumoSucesso() throws GatewayException {
        when(repository.findByEan("7891234560010")).thenReturn(Optional.of(produtoInsumo));

        Produto result = gateway.execute("7891234560010");

        assertNotNull(result);
        assertEquals("7891234560010", result.getEan());
        verify(repository, times(1)).findByEan("7891234560010");
    }

    @Test
    void executeNaoEncontradoLancaNotFoundGatewayException() {
        when(repository.findByEan("EAN_INVALIDO")).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute("EAN_INVALIDO"));

        verify(repository, times(1)).findByEan("EAN_INVALIDO");
    }
}