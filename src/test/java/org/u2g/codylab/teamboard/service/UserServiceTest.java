package org.u2g.codylab.teamboard.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.u2g.codylab.teamboard.dto.LoginRequestApiDTO;
import org.u2g.codylab.teamboard.dto.RegisterRequestApiDTO;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.exception.CustomConflictException;
import org.u2g.codylab.teamboard.exception.CustomIllegalArgumentException;
import org.u2g.codylab.teamboard.mapper.UserMapper;
import org.u2g.codylab.teamboard.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully() {

        // Arrange
        RegisterRequestApiDTO dto = new RegisterRequestApiDTO();
        dto.setUsername("test");
        dto.setPassword("Password@123");
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userMapper.toEntity(any())).thenReturn(new User());

        // Act
        ResponseEntity<Void> response = userService.register(dto);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowOnDuplicateUsername() {

        // Arrange
        RegisterRequestApiDTO dto = new RegisterRequestApiDTO();
        dto.setUsername("test");
        dto.setPassword("pass");
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(CustomConflictException.class, () -> userService.register(dto));
    }

    @Test
    void shouldThrowOnMissingUsername() {

        // Arrange
        RegisterRequestApiDTO dto = new RegisterRequestApiDTO();
        dto.setPassword("pass");

        // Act & Assert
        assertThrows(CustomIllegalArgumentException.class, () -> userService.register(dto));
    }

    @Test
    void shouldThrowOnMissingPassword() {

        // Arrange
        RegisterRequestApiDTO dto = new RegisterRequestApiDTO();
        dto.setUsername("test");

        // Act & Assert
        assertThrows(CustomIllegalArgumentException.class, () -> userService.register(dto));
    }

    @Test
    void shouldRegisterUserWithTrimmedUsername() {

        // Arrange
        RegisterRequestApiDTO dto = new RegisterRequestApiDTO();
        dto.setUsername("  test  ");
        dto.setPassword("Password@123");
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userMapper.toEntity(any())).thenReturn(new User());

        // Act & Assert
        ResponseEntity<Void> response = userService.register(dto);
        assertEquals(200, response.getStatusCode().value());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldNotRegisterUserWithSpecialCharacters() {

        // Arrange
        RegisterRequestApiDTO dto = new RegisterRequestApiDTO();
        dto.setUsername("user_123!@#");
        dto.setPassword("Password@123");

        // Act & Assert
        assertThrows(CustomIllegalArgumentException.class, () -> userService.register(dto));
    }

    @Test
    void shouldLoginSuccessfully() {

        // Arrange
        User user = new User();
        user.setUsername("test");
        user.setPassword("encoded");
        RegisterRequestApiDTO dto = new RegisterRequestApiDTO();
        dto.setUsername("test");
        dto.setPassword("Password@123");
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password@123", "encoded")).thenReturn(true);

        var loginRequest = new LoginRequestApiDTO();
        loginRequest.setUsername("test");
        loginRequest.setPassword("Password@123");

        // Act & Assert
        assertTrue(userService.login(loginRequest).isPresent());
    }

    @Test
    void shouldFailLoginWithWrongPassword() {

        // Arrange
        User user = new User();
        user.setUsername("test");
        user.setPassword("encoded");
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encoded")).thenReturn(false);

        var loginRequest = new LoginRequestApiDTO();
        loginRequest.setUsername("test");
        loginRequest.setPassword("wrongpass");

        // Act & Assert
        assertTrue(userService.login(loginRequest).isEmpty());
    }

    @Test
    void shouldFailLoginWithNonExistingUser() {

        // Arrange
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());

        var loginRequest = new LoginRequestApiDTO();
        loginRequest.setUsername("nouser");
        loginRequest.setPassword("Password@123");

        // Act & Assert
        assertTrue(userService.login(loginRequest).isEmpty());
    }
}
