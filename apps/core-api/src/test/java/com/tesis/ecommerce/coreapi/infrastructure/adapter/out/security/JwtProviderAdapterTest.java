package com.tesis.ecommerce.coreapi.infrastructure.adapter.out.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderAdapterTest {

    private JwtProviderAdapter jwtProvider;

    @BeforeEach
    void setUp() {
        String secret = "my-super-secret-key-that-should-be-at-least-32-characters-long-in-production";
        long expirationTime = 86400000;
        jwtProvider = new JwtProviderAdapter(secret, expirationTime);
    }

    @Test
    void testGenerateAndValidateToken() {
        Long userId = 1L;
        String email = "test@example.com";

        String token = jwtProvider.generateToken(userId, email);

        assertNotNull(token);
        assertTrue(jwtProvider.validateToken(token));
        assertEquals(userId, jwtProvider.extractUserId(token));
        assertEquals(email, jwtProvider.extractEmail(token));
    }

    @Test
    void testValidateInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertFalse(jwtProvider.validateToken(invalidToken));
    }
}

