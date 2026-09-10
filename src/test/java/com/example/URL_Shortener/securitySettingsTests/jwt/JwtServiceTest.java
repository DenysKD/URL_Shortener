package com.example.URL_Shortener.securitySettingsTests.jwt;

import com.example.URL_Shortener.securitySettings.entity.Role;
import com.example.URL_Shortener.securitySettings.entity.User;
import com.example.URL_Shortener.securitySettings.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "404E635266556A586E3272357538782F413F4428472B4B6250655368566D5971");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
    }

    @Test
    void shouldGenerateTokenContainingUsername() {
        User user = new User("Denys", "encodedPassword", Role.USER);

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertEquals("Denys", jwtService.extractUserName(token));
    }

    @Test
    void shouldValidateTokenForMatchingUser() {
        User user = new User("Denys", "encodedPassword", Role.USER);
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void shouldInvalidateTokenForDifferentUser() {
        User user = new User("Denys", "encodedPassword", Role.USER);
        String token = jwtService.generateToken(user);

        User otherUser = new User("Someone", "encodedPassword", Role.USER);

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void shouldThrowWhenTokenAlreadyExpired() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
        User user = new User("Denys", "encodedPassword", Role.USER);
        String token = jwtService.generateToken(user);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, user));
    }
}
