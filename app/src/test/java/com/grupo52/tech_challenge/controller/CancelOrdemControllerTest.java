package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.usecase.CancelOrdemUseCase;
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
@WebMvcTest(controllers = CancelOrdemController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class CancelOrdemControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CancelOrdemUseCase cancelOrdemUseCase;

    @Test
    void cancelarSucesso() throws Exception {
        when(cancelOrdemUseCase.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.cancelada(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/cancelar", 1L))
                .andExpect(status().isOk());

        verify(cancelOrdemUseCase, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(cancelOrdemUseCase);
    }
}