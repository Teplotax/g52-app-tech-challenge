package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import com.grupo52.tech_challenge.gateway.impl.DeleteProdutoGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProdutoGatewayImplTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private DeleteProdutoGatewayImpl gateway;

    @Test
    void executeSucessoPeca() throws GatewayException {
        when(repository.existsByIdAndTipoProduto(1L, TipoProduto.PECA)).thenReturn(true);

        gateway.execute(1L, TipoProduto.PECA);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void executeSucessoInsumo() throws GatewayException {
        when(repository.existsByIdAndTipoProduto(2L, TipoProduto.INSUMO)).thenReturn(true);

        gateway.execute(2L, TipoProduto.INSUMO);

        verify(repository, times(1)).deleteById(2L);
    }

    @Test
    void executeNaoEncontradoLancaNotFoundGatewayException() {
        when(repository.existsByIdAndTipoProduto(1L, TipoProduto.PECA)).thenReturn(false);

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(1L, TipoProduto.PECA));

        verify(repository, never()).deleteById(any());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        when(repository.existsByIdAndTipoProduto(1L, TipoProduto.PECA)).thenReturn(true);
        doThrow(new RuntimeException("erro")).when(repository).deleteById(1L);

        assertThrows(GatewayException.class, () -> gateway.execute(1L, TipoProduto.PECA));
    }
}