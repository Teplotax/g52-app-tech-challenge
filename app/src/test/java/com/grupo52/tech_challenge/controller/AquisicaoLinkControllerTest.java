package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemPeca;
import com.grupo52.tech_challenge.domain.OrdemServico;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.gateway.ApprovalTokenGateway;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.usecase.ConfirmAquisicaoUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AquisicaoLinkController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class AquisicaoLinkControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ApprovalTokenGateway approvalTokenGateway;

    @MockitoBean
    private FindOrdemGateway findOrdemGateway;

    @MockitoBean
    private ConfirmAquisicaoUseCase confirmAquisicaoUseCase;

    private Ordem osAguardandoAquisicao() {
        OrdemServico servico = OrdemServico.builder()
                .servico(Servico.builder().nome("Troca de disco").build())
                .aprovado(true)
                .pecas(new ArrayList<>(List.of(
                        OrdemPeca.builder()
                                .peca(Peca.builder().id(100L).nome("Disco de freio dianteiro").preco(new BigDecimal("90.00")).build())
                                .quantidade(2).precoTotal(new BigDecimal("180.00")).reservado(false)
                                .build()
                )))
                .insumos(new ArrayList<>())
                .build();

        return Ordem.builder()
                .id(1L)
                .status(Status.AGUARDANDO_AQUISICAO)
                .servicosDesejados(new ArrayList<>(List.of(servico)))
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .build();
    }

    @Test
    void confirmPageTokenInvalido() throws Exception {
        when(approvalTokenGateway.isValid(1L, "token-invalido")).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.get("/aquisicao/{osId}", 1L)
                        .param("token", "token-invalido"))
                .andExpect(status().is(403))
                .andExpect(content().string(containsString("Link inválido ou expirado")));

        verifyNoInteractions(findOrdemGateway);
    }

    @Test
    void confirmPageOsNaoAguardandoAquisicao() throws Exception {
        when(approvalTokenGateway.isValid(1L, "token-valido")).thenReturn(true);
        when(findOrdemGateway.execute(1L)).thenReturn(Ordem.builder().status(Status.APROVADA).build());

        mvc.perform(MockMvcRequestBuilders.get("/aquisicao/{osId}", 1L)
                        .param("token", "token-valido"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Aquisição não pendente")));
    }

    @Test
    void confirmPageExibeFormulario() throws Exception {
        when(approvalTokenGateway.isValid(1L, "token-valido")).thenReturn(true);
        when(findOrdemGateway.execute(1L)).thenReturn(osAguardandoAquisicao());

        mvc.perform(MockMvcRequestBuilders.get("/aquisicao/{osId}", 1L)
                        .param("token", "token-valido"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Confirmar aquisição")))
                .andExpect(content().string(containsString("Disco de freio dianteiro")));
    }

    @Test
    void confirmPageErroInesperado() throws Exception {
        when(approvalTokenGateway.isValid(1L, "token-valido")).thenReturn(true);
        doThrow(RuntimeException.class).when(findOrdemGateway).execute(1L);

        mvc.perform(MockMvcRequestBuilders.get("/aquisicao/{osId}", 1L)
                        .param("token", "token-valido"))
                .andExpect(status().is(500))
                .andExpect(content().string(containsString("Não foi possível carregar")));
    }

    @Test
    void confirmTokenInvalido() throws Exception {
        when(approvalTokenGateway.isValid(1L, "token-invalido")).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.post("/aquisicao/{osId}", 1L)
                        .param("token", "token-invalido"))
                .andExpect(status().is(403))
                .andExpect(content().string(containsString("Link inválido ou expirado")));

        verifyNoInteractions(confirmAquisicaoUseCase);
    }

    @Test
    void confirmOsNaoAguardandoAquisicao() throws Exception {
        when(approvalTokenGateway.isValid(1L, "token-valido")).thenReturn(true);
        when(findOrdemGateway.execute(1L)).thenReturn(Ordem.builder().status(Status.APROVADA).build());

        mvc.perform(MockMvcRequestBuilders.post("/aquisicao/{osId}", 1L)
                        .param("token", "token-valido"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Aquisição não pendente")));

        verifyNoInteractions(confirmAquisicaoUseCase);
    }

    @Test
    void confirmSucesso() throws Exception {
        when(approvalTokenGateway.isValid(1L, "token-valido")).thenReturn(true);
        when(findOrdemGateway.execute(1L)).thenReturn(osAguardandoAquisicao());
        when(confirmAquisicaoUseCase.execute(1L)).thenReturn(osAguardandoAquisicao());

        mvc.perform(MockMvcRequestBuilders.post("/aquisicao/{osId}", 1L)
                        .param("token", "token-valido"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Aquisição confirmada!")));

        verify(confirmAquisicaoUseCase, times(1)).execute(1L);
    }

    @Test
    void confirmErroInesperado() throws Exception {
        when(approvalTokenGateway.isValid(1L, "token-valido")).thenReturn(true);
        when(findOrdemGateway.execute(1L)).thenReturn(osAguardandoAquisicao());
        doThrow(RuntimeException.class).when(confirmAquisicaoUseCase).execute(1L);

        mvc.perform(MockMvcRequestBuilders.post("/aquisicao/{osId}", 1L)
                        .param("token", "token-valido"))
                .andExpect(status().is(500))
                .andExpect(content().string(containsString("Não foi possível confirmar")));
    }
}