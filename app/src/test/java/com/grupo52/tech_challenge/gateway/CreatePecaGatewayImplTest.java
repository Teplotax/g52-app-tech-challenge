package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.AplicacaoProdutoRepository;
import com.grupo52.tech_challenge.gateway.database.repository.ModeloRepository;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import com.grupo52.tech_challenge.gateway.impl.CreatePecaGatewayImpl;
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
class CreatePecaGatewayImplTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ModeloRepository modeloRepository;

    @Mock
    private AplicacaoProdutoRepository aplicacaoProdutoRepository;

    @InjectMocks
    private CreatePecaGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        Peca peca = Peca.builder()
                .sku("SKU-001").ean("7891234560001").nome("Pastilha de freio")
                .preco(new BigDecimal("35.00")).estoque(10).estoqueMinimo(2)
                .tipoPeca(TipoPeca.PASTILHA_FREIO).aplicacoes(new ArrayList<>())
                .build();

        ProdutoDatabase salvo = ProdutoDatabase.builder()
                .id(1L).sku("SKU-001").ean("7891234560001").nome("Pastilha de freio")
                .preco(new BigDecimal("35.00")).estoque(10).estoqueReservado(0).estoqueMinimo(2)
                .tipoPeca(TipoPeca.PASTILHA_FREIO).aplicacoes(new ArrayList<>())
                .build();

        when(produtoRepository.save(any(ProdutoDatabase.class))).thenReturn(salvo);

        Peca result = gateway.execute(peca);

        assertNotNull(result);
        assertEquals("SKU-001", result.getSku());
        verify(produtoRepository, times(1)).save(any(ProdutoDatabase.class));
    }

    @Test
    void executeSkuOuEanDuplicadoLancaGatewayException() {
        Peca peca = Peca.builder().sku("SKU-001").ean("7891234560001").aplicacoes(new ArrayList<>()).build();

        when(produtoRepository.save(any(ProdutoDatabase.class))).thenThrow(new DataIntegrityViolationException("duplicate key"));

        GatewayException exception = assertThrows(GatewayException.class, () -> gateway.execute(peca));

        assertEquals(409, exception.getStatus());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        Peca peca = Peca.builder().sku("SKU-001").ean("7891234560001").aplicacoes(new ArrayList<>()).build();

        when(produtoRepository.save(any(ProdutoDatabase.class))).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(peca));
    }
}