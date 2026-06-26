package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Marca;
import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.gateway.ListMarcasGateway;
import com.grupo52.tech_challenge.gateway.ListModelosByMarcaGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MarcaController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
public class MarcaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ListMarcasGateway listMarcasGateway;

    @MockitoBean
    private ListModelosByMarcaGateway listModelosByMarcaGateway;

    @Test
    @WithMockUser
    public void listMarcasSucesso() throws Exception {
        when(listMarcasGateway.execute()).thenReturn(List.of(
                Marca.builder().id(1L).nome("Toyota").build()
        ));

        mvc.perform(MockMvcRequestBuilders.get("/marcas"))
                .andExpect(status().isOk());

        verify(listMarcasGateway, times(1)).execute();
        verifyNoMoreInteractions(listMarcasGateway);
    }

    @Test
    @WithMockUser
    public void listModelosByMarcaSucesso() throws Exception {
        when(listModelosByMarcaGateway.execute(any(Long.class))).thenReturn(List.of(
                Modelo.builder().id(1L).nome("Corolla").build()
        ));

        mvc.perform(MockMvcRequestBuilders.get("/marcas/{marcaId}/modelos", 1L))
                .andExpect(status().isOk());

        verify(listModelosByMarcaGateway, times(1)).execute(1L);
        verifyNoMoreInteractions(listModelosByMarcaGateway);
    }
}