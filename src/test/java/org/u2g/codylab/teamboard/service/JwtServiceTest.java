package org.u2g.codylab.teamboard.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Test
    void shouldGenerateTokenAndExtractUsername() {

        // Arrange
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "expiration", "3600000");
        String username = "user";

        // Act & Assert
        String token = jwtService.generateToken(username);
        assertNotNull(token);
        assertEquals(username, jwtService.extractUsername(token));
    }

    @Test
    void shouldValidateToken() {

        // Arrange
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "expiration", "3600000");
        String username = "user";

        // Act & Assert
        String token = jwtService.generateToken(username);
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void shouldReturnFalseForInvalidToken() {

        // Arrange
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "expiration", "3600000");

        // Act & Assert
        assertFalse(jwtService.validateToken("invalid.token.value"));
    }

    @Test
    void shouldDetectExpiredToken() {

        // Arrange
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "expiration", "-1000"); // token già scaduto
        String username = "user";

        // Act & Assert
        String token = jwtService.generateToken(username);
        assertFalse(jwtService.validateToken(token));
    }
}

