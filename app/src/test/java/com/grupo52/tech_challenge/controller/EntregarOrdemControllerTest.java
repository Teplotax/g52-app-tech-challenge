package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.usecase.EntregarOrdemUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EntregarOrdemController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class EntregarOrdemControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private EntregarOrdemUseCase entregarOrdemUseCase;

    @Test
    void entregarSucesso() throws Exception {
        when(entregarOrdemUseCase.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.entregue(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/entregar", 1L))
                .andExpect(status().isOk());

        verify(entregarOrdemUseCase, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(entregarOrdemUseCase);
    }
}