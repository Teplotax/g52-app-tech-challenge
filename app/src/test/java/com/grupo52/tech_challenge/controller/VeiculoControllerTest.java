package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Marca;
import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.gateway.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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
@WebMvcTest(controllers = VeiculoController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
public class VeiculoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CreateVeiculoGateway createVeiculoGateway;

    @MockitoBean
    private FindVeiculoGateway findVeiculoGateway;

    @MockitoBean
    private FindVeiculoByPlacaGateway findVeiculoByPlacaGateway;

    @MockitoBean
    private ListVeiculosGateway listVeiculosGateway;

    @MockitoBean
    private UpdateVeiculoGateway updateVeiculoGateway;

    @MockitoBean
    private DeleteVeiculoGateway deleteVeiculoGateway;

    private static final String VALID_PLACA_MERCOSUL = "ABC1D23";
    private static final String VALID_PLACA_ANTIGA = "ABC1234";

    private Veiculo veiculoFixture() {
        return Veiculo.builder()
                .id(1L).placa(VALID_PLACA_MERCOSUL).cor("Prata").ano(2020).clienteId(1L)
                .marca(Marca.builder().id(1L).nome("Toyota").build())
                .modelo(Modelo.builder().id(1L).nome("Corolla").build())
                .build();
    }

    @Test
    @WithMockUser
    public void createVeiculoSucesso() throws Exception {
        when(createVeiculoGateway.execute(any(Veiculo.class))).thenReturn(veiculoFixture());

        mvc.perform(MockMvcRequestBuilders.post("/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":1,\"modeloId\":1,\"placa\":\"" + VALID_PLACA_MERCOSUL + "\",\"cor\":\"Prata\",\"ano\":2020}"))
                .andExpect(status().isCreated());

        verify(createVeiculoGateway, times(1)).execute(any(Veiculo.class));
        verifyNoMoreInteractions(createVeiculoGateway);
    }

    @Test
    @WithMockUser
    public void findVeiculoSucesso() throws Exception {
        when(findVeiculoGateway.execute(any(Long.class))).thenReturn(veiculoFixture());

        mvc.perform(MockMvcRequestBuilders.get("/veiculos/{veiculoId}", 1L))
                .andExpect(status().isOk());

        verify(findVeiculoGateway, times(1)).execute(1L);
        verifyNoMoreInteractions(findVeiculoGateway);
    }

    @Test
    @WithMockUser
    public void findVeiculoByPlacaSucesso() throws Exception {
        when(findVeiculoByPlacaGateway.execute(any(String.class))).thenReturn(veiculoFixture());

        mvc.perform(MockMvcRequestBuilders.get("/veiculos/placa/{placa}", VALID_PLACA_MERCOSUL))
                .andExpect(status().isOk());

        verify(findVeiculoByPlacaGateway, times(1)).execute(VALID_PLACA_MERCOSUL);
        verifyNoMoreInteractions(findVeiculoByPlacaGateway);
    }

    @Test
    @WithMockUser
    public void listVeiculosSucesso() throws Exception {
        when(listVeiculosGateway.execute(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(veiculoFixture())));

        mvc.perform(MockMvcRequestBuilders.get("/veiculos"))
                .andExpect(status().isOk());

        verify(listVeiculosGateway, times(1)).execute(any(Pageable.class));
        verifyNoMoreInteractions(listVeiculosGateway);
    }

    @Test
    @WithMockUser
    public void updateVeiculoSucesso() throws Exception {
        when(updateVeiculoGateway.execute(any(Veiculo.class))).thenReturn(veiculoFixture());

        mvc.perform(MockMvcRequestBuilders.put("/veiculos/{veiculoId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cor\":\"Preto\"}"))
                .andExpect(status().isOk());

        verify(updateVeiculoGateway, times(1)).execute(any(Veiculo.class));
        verifyNoMoreInteractions(updateVeiculoGateway);
    }

    @Test
    @WithMockUser
    public void deleteVeiculoSucesso() throws Exception {
        doNothing().when(deleteVeiculoGateway).execute(any(Long.class));

        mvc.perform(MockMvcRequestBuilders.delete("/veiculos/{veiculoId}", 1L))
                .andExpect(status().isNoContent());

        verify(deleteVeiculoGateway, times(1)).execute(1L);
        verifyNoMoreInteractions(deleteVeiculoGateway);
    }
}