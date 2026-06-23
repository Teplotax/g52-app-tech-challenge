package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.gateway.ApprovalTokenGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Component
public class ApprovalTokenGatewayImpl implements ApprovalTokenGateway {

    private static final String ALGORITHM = "HmacSHA256";

    @Value("${app.approval.secret}")
    private String secret;

    @Value("${app.approval.ttl-minutes}")
    private long ttlMinutes;

    @Override
    public String generate(Long osId) {
        long expiry = Instant.now().plusSeconds(ttlMinutes * 60).getEpochSecond();
        String payload = osId + ":" + expiry;
        String signature = sign(payload);
        return base64(payload + ":" + signature);
    }

    @Override
    public boolean isValid(Long osId, String token) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":");
            if (parts.length != 3) {
                return false;
            }
            Long tokenOsId = Long.valueOf(parts[0]);
            long expiry = Long.parseLong(parts[1]);
            String signature = parts[2];

            if (!tokenOsId.equals(osId)) {
                return false;
            }
            if (Instant.now().getEpochSecond() > expiry) {
                return false;
            }
            String expected = sign(parts[0] + ":" + parts[1]);
            return constantTimeEquals(expected, signature);
        } catch (Exception e) {
            return false;
        }
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM));
            byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao assinar token de aprovação", e);
        }
    }

    private String base64(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
