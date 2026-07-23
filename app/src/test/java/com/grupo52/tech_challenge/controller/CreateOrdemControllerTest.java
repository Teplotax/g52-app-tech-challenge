package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.usecase.CreateOrdemUseCase;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CreateOrdemController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class CreateOrdemControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CreateOrdemUseCase createOrdemUseCase;

    @Test
    void criarOSSucesso() throws Exception {
        when(createOrdemUseCase.execute(any())).thenReturn(OrdemDeServicoFixture.recebida(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placa\":\"ABC1A23\",\"tagChave\":\"001\",\"servicosDesejados\":[1]}"))
                .andExpect(status().isCreated());

        verify(createOrdemUseCase, times(1)).execute(any());
        verifyNoMoreInteractions(createOrdemUseCase);
    }
}