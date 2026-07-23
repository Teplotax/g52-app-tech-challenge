package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import com.grupo52.tech_challenge.gateway.impl.FindPecaGatewayImpl;
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
class FindPecaGatewayImplTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private FindPecaGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        ProdutoDatabase pecaDatabase = ProdutoDatabase.builder()
                .id(1L).sku("SKU-001").ean("7891234560001").nome("Pastilha de freio")
                .preco(new BigDecimal("35.00")).estoque(10).estoqueReservado(0).estoqueMinimo(2)
                .tipoProduto(TipoProduto.PECA).tipoPeca(TipoPeca.PASTILHA_FREIO).aplicacoes(new ArrayList<>())
                .build();

        when(repository.findByIdAndTipoProduto(1L, TipoProduto.PECA)).thenReturn(Optional.of(pecaDatabase));

        Peca result = gateway.execute(1L);

        assertNotNull(result);
        assertEquals("SKU-001", result.getSku());
        verify(repository, times(1)).findByIdAndTipoProduto(1L, TipoProduto.PECA);
    }

    @Test
    void executeNaoEncontradaLancaNotFoundGatewayException() {
        when(repository.findByIdAndTipoProduto(1L, TipoProduto.PECA)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(1L));
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        when(repository.findByIdAndTipoProduto(1L, TipoProduto.PECA)).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(1L));
    }
}