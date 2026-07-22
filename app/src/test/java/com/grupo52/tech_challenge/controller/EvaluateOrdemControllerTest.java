package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.usecase.EvaluateOrdemUseCase;
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
@WebMvcTest(controllers = EvaluateOrdemController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class EvaluateOrdemControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private EvaluateOrdemUseCase evaluateOrdemUseCase;

    @Test
    void diagnosticarSucesso() throws Exception {
        when(evaluateOrdemUseCase.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.emDiagnostico(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/diagnosticar", 1L))
                .andExpect(status().isOk());

        verify(evaluateOrdemUseCase, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(evaluateOrdemUseCase);
    }
}