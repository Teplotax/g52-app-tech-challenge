package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.gateway.impl.ApprovalTokenGatewayImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class ApprovalTokenGatewayImplTest {

    private ApprovalTokenGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        gateway = new ApprovalTokenGatewayImpl();
        ReflectionTestUtils.setField(gateway, "secret", "test-secret-key");
        ReflectionTestUtils.setField(gateway, "ttlMinutes", 30L);
    }

    @Test
    void generateEIsValidSucesso() {
        String token = gateway.generate(1L);

        assertNotNull(token);
        assertTrue(gateway.isValid(1L, token));
    }

    @Test
    void isValidRejeitaOsIdDiferente() {
        String token = gateway.generate(1L);

        assertFalse(gateway.isValid(2L, token));
    }

    @Test
    void isValidRejeitaTokenExpirado() {
        ReflectionTestUtils.setField(gateway, "ttlMinutes", -1L);
        String token = gateway.generate(1L);

        assertFalse(gateway.isValid(1L, token));
    }

    @Test
    void isValidRejeitaTokenAdulterado() {
        String token = gateway.generate(1L);

        String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
        String[] parts = decoded.split(":");
        String adulterado = "2:" + parts[1] + ":" + parts[2];
        String tokenAdulterado = Base64.getUrlEncoder().withoutPadding().encodeToString(adulterado.getBytes(StandardCharsets.UTF_8));

        assertFalse(gateway.isValid(2L, tokenAdulterado));
    }

    @Test
    void isValidRejeitaTokenMalFormado() {
        assertFalse(gateway.isValid(1L, "token-invalido-nao-base64!!"));
    }

    @Test
    void isValidRejeitaTokenComPartesFaltando() {
        String semAssinatura = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("1:123456".getBytes(StandardCharsets.UTF_8));

        assertFalse(gateway.isValid(1L, semAssinatura));
    }

    @Test
    void tokensDiferentesParaOsIdsDiferentes() {
        String token1 = gateway.generate(1L);
        String token2 = gateway.generate(2L);

        assertNotEquals(token1, token2);
    }
}