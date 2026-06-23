package com.grupo52.tech_challenge.gateway;

public interface ApprovalTokenGateway {
    String generate(Long osId);
    boolean isValid(Long osId, String token);
}
