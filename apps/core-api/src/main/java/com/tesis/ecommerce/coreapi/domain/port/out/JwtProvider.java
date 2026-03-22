package com.tesis.ecommerce.coreapi.domain.port.out;

import java.util.UUID;

public interface JwtProvider {
    String generateToken(UUID userId, String email);
    UUID extractUserId(String token);
    String extractEmail(String token);
    boolean validateToken(String token);
}

