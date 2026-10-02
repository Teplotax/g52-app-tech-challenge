package com.grupo52.tech_challenge.config;

import com.grupo52.tech_challenge.controller.ApproveOrdemController;
import com.grupo52.tech_challenge.controller.FindOrdemController;
import com.grupo52.tech_challenge.controller.ListOrdemController;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.gateway.ListOrdemGateway;
import com.grupo52.tech_challenge.usecase.ApproveOrdemUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// token de cliente (cpf) só consulta e aprova as próprias OS
@WebMvcTest(controllers = {FindOrdemController.class, ListOrdemController.class, ApproveOrdemController.class})
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.security.jwt.jwk-set-uri=http://localhost/.well-known/jwks.json")
class AcessoClienteTest {

    // documento do cliente do fixture
    private static final String CPF_DONO = "123.456.789-00";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private FindOrdemGateway findOrdemGateway;

    @MockitoBean
    private ListOrdemGateway listOrdemGateway;

    @MockitoBean
    private ApproveOrdemUseCase approveOrdemUseCase;

    private JwtRequestPostProcessor cliente(String cpf) {
        return jwt().jwt(j -> j.subject("1").claim("cpf", cpf))
                .authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }

    private JwtRequestPostProcessor admin() {
        return jwt().jwt(j -> j.subject("g52-oficina-admin"))
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Test
    void clienteConsultaPropriaOs() throws Exception {
        when(findOrdemGateway.execute(1L)).thenReturn(OrdemDeServicoFixture.emDiagnostico(1L));

        mvc.perform(MockMvcRequestBuilders.get("/ordensDeServico/{osId}", 1L).with(cliente(CPF_DONO)))
                .andExpect(status().isOk());
    }

    @Test
    void clienteNaoConsultaOsDeOutroCliente() throws Exception {
        when(findOrdemGateway.execute(1L)).thenReturn(OrdemDeServicoFixture.emDiagnostico(1L));

        mvc.perform(MockMvcRequestBuilders.get("/ordensDeServico/{osId}", 1L).with(cliente("55563271064")))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminConsultaQualquerOs() throws Exception {
        when(findOrdemGateway.execute(1L)).thenReturn(OrdemDeServicoFixture.emDiagnostico(1L));

        mvc.perform(MockMvcRequestBuilders.get("/ordensDeServico/{osId}", 1L).with(admin()))
                .andExpect(status().isOk());
    }

    @Test
    void listagemDoClienteFiltraPeloCpfDoToken() throws Exception {
        when(listOrdemGateway.execute(any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mvc.perform(MockMvcRequestBuilders.get("/ordensDeServico")
                        .param("documentoCliente", "outro-documento")
                        .with(cliente(CPF_DONO)))
                .andExpect(status().isOk());

        verify(listOrdemGateway).execute(any(), eq(CPF_DONO), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void listagemDoAdminRespeitaOFiltro() throws Exception {
        when(listOrdemGateway.execute(any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mvc.perform(MockMvcRequestBuilders.get("/ordensDeServico")
                        .param("documentoCliente", "55563271064")
                        .with(admin()))
                .andExpect(status().isOk());

        verify(listOrdemGateway).execute(any(), eq("55563271064"), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void clienteAprovaPropriaOs() throws Exception {
        when(findOrdemGateway.execute(1L)).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(1L));
        when(approveOrdemUseCase.approveAll(1L)).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/aprovar", 1L).with(cliente(CPF_DONO)))
                .andExpect(status().isOk());

        verify(approveOrdemUseCase).approveAll(1L);
    }

    @Test
    void clienteNaoAprovaOsDeOutroCliente() throws Exception {
        when(findOrdemGateway.execute(1L)).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/aprovar", 1L).with(cliente("55563271064")))
                .andExpect(status().isNotFound());

        verifyNoInteractions(approveOrdemUseCase);
    }

    @Test
    void clienteSemClaimCpfNaoVeNada() throws Exception {
        when(findOrdemGateway.execute(1L)).thenReturn(OrdemDeServicoFixture.emDiagnostico(1L));

        mvc.perform(MockMvcRequestBuilders.get("/ordensDeServico/{osId}", 1L)
                        .with(jwt().jwt(j -> j.subject("1")).authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void clienteNaoExecutaAcoesAdministrativasDaOs() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/executar", 1L).with(cliente(CPF_DONO)))
                .andExpect(status().isForbidden());
    }
}
