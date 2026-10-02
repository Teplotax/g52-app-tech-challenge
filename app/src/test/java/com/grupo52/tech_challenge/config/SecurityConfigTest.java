package com.grupo52.tech_challenge.config;

import com.grupo52.tech_challenge.controller.MarcaController;
import com.grupo52.tech_challenge.gateway.ListMarcasGateway;
import com.grupo52.tech_challenge.gateway.ListModelosByMarcaGateway;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityConfigTest {

    private static final String ISSUER = "g52-lambda-auth";
    private static final String AUDIENCE = "tech-challenge-api";

    @Nested
    @WebMvcTest(controllers = MarcaController.class)
    @Import(SecurityConfig.class)
    @TestPropertySource(properties = "app.security.jwt.jwk-set-uri=http://localhost/.well-known/jwks.json")
    class AutenticacaoLigada {

        @Autowired
        private MockMvc mvc;

        @MockitoBean
        private ListMarcasGateway listMarcasGateway;

        @MockitoBean
        private ListModelosByMarcaGateway listModelosByMarcaGateway;

        @Test
        void semTokenRetorna401() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/marcas"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void comTokenValidoRetorna200() throws Exception {
            when(listMarcasGateway.execute()).thenReturn(List.of());

            mvc.perform(MockMvcRequestBuilders.get("/marcas")
                            .with(jwt().jwt(j -> j.subject("1").claim("cpf", "55563271064"))))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @WebMvcTest(controllers = MarcaController.class)
    @Import(SecurityConfig.class)
    @TestPropertySource(properties = "app.security.enabled=false")
    class AutenticacaoDesligada {

        @Autowired
        private MockMvc mvc;

        @MockitoBean
        private ListMarcasGateway listMarcasGateway;

        @MockitoBean
        private ListModelosByMarcaGateway listModelosByMarcaGateway;

        @Test
        void semTokenRetorna200() throws Exception {
            when(listMarcasGateway.execute()).thenReturn(List.of());

            mvc.perform(MockMvcRequestBuilders.get("/marcas"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class ValidacaoDoToken {

        private Jwt.Builder token() {
            Instant now = Instant.now();
            return Jwt.withTokenValue("token")
                    .header("alg", "RS256")
                    .subject("1")
                    .issuer(ISSUER)
                    .audience(List.of(AUDIENCE))
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(3600));
        }

        private boolean valido(Jwt jwt) {
            return !SecurityConfig.tokenValidator(ISSUER, AUDIENCE).validate(jwt).hasErrors();
        }

        @Test
        void tokenEmitidoPelaLambdaEhValido() {
            assertThat(valido(token().build())).isTrue();
        }

        @Test
        void issuerDiferenteEhRejeitado() {
            assertThat(valido(token().issuer("outro-emissor").build())).isFalse();
        }

        @Test
        void audienceDiferenteEhRejeitada() {
            assertThat(valido(token().audience(List.of("outra-api")).build())).isFalse();
        }

        @Test
        void tokenSemAudienceEhRejeitado() {
            assertThat(valido(token().claims(c -> c.remove("aud")).build())).isFalse();
        }

        @Test
        void tokenExpiradoEhRejeitado() {
            Instant passado = Instant.now().minusSeconds(7200);
            assertThat(valido(token().issuedAt(passado).expiresAt(passado.plusSeconds(60)).build())).isFalse();
        }
    }
}
