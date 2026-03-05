package org.u2g.codylab.teamboard.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.u2g.codylab.teamboard.dto.LoginRequestApiDTO;
import org.u2g.codylab.teamboard.dto.RegisterRequestApiDTO;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.exception.CustomAnauthorizedException;
import org.u2g.codylab.teamboard.exception.CustomConflictException;
import org.u2g.codylab.teamboard.exception.CustomNotFoundException;
import org.u2g.codylab.teamboard.mapper.UserMapper;
import org.u2g.codylab.teamboard.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        RegisterRequestApiDTO dto = new RegisterRequestApiDTO();
        dto.setUsername("test");
        dto.setPassword("Password@123");

        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password@123")).thenReturn("encoded");
        when(userMapper.toEntity(dto)).thenReturn(new User());

        ResponseEntity<Void> response = userService.register(dto);

        assertEquals(200, response.getStatusCode().value());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {

        RegisterRequestApiDTO dto = new RegisterRequestApiDTO();
        dto.setUsername("test");

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(new User()));

        assertThrows(CustomConflictException.class,
                () -> userService.register(dto));
    }


    @Test
    void shouldLoginSuccessfully() {

        User user = new User();
        user.setUsername("test");
        user.setPassword("encoded");

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("Password@123", "encoded"))
                .thenReturn(true);

        LoginRequestApiDTO request = new LoginRequestApiDTO();
        request.setUsername("test");
        request.setPassword("Password@123");

        User result = userService.login(request);

        assertNotNull(result);
        assertEquals("test", result.getUsername());
    }

    @Test
    void shouldThrowWhenPasswordIsWrong() {

        User user = new User();
        user.setUsername("test");
        user.setPassword("encoded");

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrongpass", "encoded"))
                .thenReturn(false);

        LoginRequestApiDTO request = new LoginRequestApiDTO();
        request.setUsername("test");
        request.setPassword("wrongpass");

        assertThrows(CustomAnauthorizedException.class,
                () -> userService.login(request));
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {

        when(userRepository.findByUsername("nouser"))
                .thenReturn(Optional.empty());

        LoginRequestApiDTO request = new LoginRequestApiDTO();
        request.setUsername("nouser");
        request.setPassword("Password@123");

        assertThrows(CustomAnauthorizedException.class,
                () -> userService.login(request));
    }

    @Test
    void shouldReturnUserById() {

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowWhenUserNotFoundById() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class,
                () -> userService.getUserById(1L));
    }

    @Test
    void shouldDeleteUserSuccessfully() {

        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUserById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistingUser() {

        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(CustomNotFoundException.class,
                () -> userService.deleteUserById(1L));
    }
}