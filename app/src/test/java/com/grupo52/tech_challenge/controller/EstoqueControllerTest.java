package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.gateway.FindProdutoByEanGateway;
import com.grupo52.tech_challenge.gateway.MovimentarEstoqueGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EstoqueController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class EstoqueControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private MovimentarEstoqueGateway movimentarEstoqueGateway;

    @MockitoBean
    private FindProdutoByEanGateway findProdutoByEanGateway;

    private Peca pecaFixture() {
        return Peca.builder()
                .id(1L).ean("7891234560001").nome("Pastilha de freio")
                .preco(new BigDecimal("35.00")).estoque(10)
                .tipoProduto(TipoProduto.PECA).aplicacoes(new ArrayList<>())
                .build();
    }

    @Test
    void findByEanSucesso() throws Exception {
        when(findProdutoByEanGateway.execute(any(String.class))).thenReturn(pecaFixture());

        mvc.perform(MockMvcRequestBuilders.get("/produtos/ean/{ean}", "7891234560001"))
                .andExpect(status().isOk());

        verify(findProdutoByEanGateway, times(1)).execute("7891234560001");
        verifyNoMoreInteractions(findProdutoByEanGateway);
    }

    @Test
    void entradaEstoqueSucesso() throws Exception {
        when(movimentarEstoqueGateway.entrada(any(), any())).thenReturn(List.of(pecaFixture()));

        mvc.perform(MockMvcRequestBuilders.post("/produtos/estoque/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"ean\":\"7891234560001\",\"quantidade\":5}]"))
                .andExpect(status().isOk());

        verify(movimentarEstoqueGateway, times(1)).entrada(any(), any());
        verifyNoMoreInteractions(movimentarEstoqueGateway);
    }

    @Test
    void saidaEstoqueSucesso() throws Exception {
        when(movimentarEstoqueGateway.saida(any(), any())).thenReturn(List.of(pecaFixture()));

        mvc.perform(MockMvcRequestBuilders.post("/produtos/estoque/saida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"ean\":\"7891234560001\",\"quantidade\":2}]"))
                .andExpect(status().isOk());

        verify(movimentarEstoqueGateway, times(1)).saida(any(), any());
        verifyNoMoreInteractions(movimentarEstoqueGateway);
    }
}