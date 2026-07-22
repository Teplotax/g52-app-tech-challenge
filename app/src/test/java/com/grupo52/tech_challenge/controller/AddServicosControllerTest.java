package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.usecase.AddServicosUseCase;
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
@WebMvcTest(controllers = AddServicosController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class AddServicosControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AddServicosUseCase addServicosUseCase;

    @Test
    void adicionarServicosSucesso() throws Exception {
        when(addServicosUseCase.execute(any())).thenReturn(OrdemDeServicoFixture.emDiagnostico(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/adicionarServicos", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"servicosNecessarios\":[2],\"justificativaNecessarios\":\"Desgaste identificado\"}"))
                .andExpect(status().isOk());

        verify(addServicosUseCase, times(1)).execute(any());
        verifyNoMoreInteractions(addServicosUseCase);
    }
}