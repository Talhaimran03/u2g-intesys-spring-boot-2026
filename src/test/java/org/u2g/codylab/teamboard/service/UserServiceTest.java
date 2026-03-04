package org.u2g.codylab.teamboard.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.u2g.codylab.teamboard.dto.LoginRequestApiDTO;
import org.u2g.codylab.teamboard.dto.RegisterRequestApiDTO;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.exception.CustomConflictException;
import org.u2g.codylab.teamboard.exception.CustomIllegalArgumentException;
import org.u2g.codylab.teamboard.exception.CustomNotFoundException;
import org.u2g.codylab.teamboard.mapper.UserMapper;
import org.u2g.codylab.teamboard.repository.UserRepository;

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

    // REGISTER

    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange
        RegisterRequestApiDTO dto = new RegisterRequestApiDTO()
                .username("joel")
                .password("83210");

        when(userRepository.findByUsername("joel")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("83210")).thenReturn("encodedPassword");
        when(userMapper.toEntity(any(RegisterRequestApiDTO.class))).thenReturn(new User());

        // Act & Assert
        assertDoesNotThrow(() -> userService.register(dto));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        // Arrange
        RegisterRequestApiDTO dto = new RegisterRequestApiDTO()
                .username("joel")
                .password("83210");

        when(userRepository.findByUsername("joel")).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(CustomConflictException.class, () -> userService.register(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUsernameIsBlank() {
        // Arrange
        RegisterRequestApiDTO dto = new RegisterRequestApiDTO()
                .username("")
                .password("83210");

        // Act & Assert
        assertThrows(CustomIllegalArgumentException.class, () -> userService.register(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenPasswordIsBlank() {
        // Arrange
        RegisterRequestApiDTO dto = new RegisterRequestApiDTO()
                .username("joel")
                .password("");

        // Act & Assert
        assertThrows(CustomIllegalArgumentException.class, () -> userService.register(dto));
        verify(userRepository, never()).save(any());
    }

    // ─── LOGIN ───────────────────────────────────────────────

    @Test
    void shouldLoginSuccessfully() {
        // Arrange
        User user = new User().setUsername("joel").setPassword("encodedPassword");

        LoginRequestApiDTO dto = new LoginRequestApiDTO()
                .username("joel")
                .password("83210");

        when(userRepository.findByUsername("joel")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("83210", "encodedPassword")).thenReturn(true);

        // Act
        Optional<User> result = userService.login(dto);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("joel", result.get().getUsername());
    }

    @Test
    void shouldReturnEmptyWhenPasswordIsWrong() {
        // Arrange
        User user = new User().setUsername("joel").setPassword("encodedPassword");

        LoginRequestApiDTO dto = new LoginRequestApiDTO()
                .username("joel")
                .password("wrongPassword");

        when(userRepository.findByUsername("joel")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act
        Optional<User> result = userService.login(dto);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        // Arrange
        LoginRequestApiDTO dto = new LoginRequestApiDTO()
                .username("unknown")
                .password("83210");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.login(dto);

        // Assert
        assertTrue(result.isEmpty());
    }

    // ─── DELETE ──────────────────────────────────────────────

    @Test
    void shouldDeleteUserSuccessfully() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act & Assert
        assertDoesNotThrow(() -> userService.deleteUserById(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenUserToDeleteNotFound() {
        // Arrange
        when(userRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(CustomNotFoundException.class, () -> userService.deleteUserById(99L));
        verify(userRepository, never()).deleteById(any());
    }
}