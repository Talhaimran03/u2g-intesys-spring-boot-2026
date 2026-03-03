package org.u2g.codylab.teamboard.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.u2g.codylab.teamboard.dto.LoginRequestApiDTO;
import org.u2g.codylab.teamboard.dto.RegisterRequestApiDTO;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.exception.CustomConflictException;
import org.u2g.codylab.teamboard.exception.CustomNotFoundException;
import org.u2g.codylab.teamboard.mapper.UserMapper;
import org.u2g.codylab.teamboard.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public ResponseEntity<Void> register(RegisterRequestApiDTO registerRequestApiDTO) {
        log.info("Registering user: {}", registerRequestApiDTO.getUsername());

        if (userRepository.findByUsername(registerRequestApiDTO.getUsername()).isPresent()) {
            throw new CustomConflictException("Username already exists: " + registerRequestApiDTO.getUsername());
        }

        registerRequestApiDTO.setPassword(passwordEncoder.encode(registerRequestApiDTO.getPassword()));
        User userEntity = userMapper.toEntity(registerRequestApiDTO);
        userRepository.save(userEntity);

        log.info("User registered: {}", registerRequestApiDTO.getUsername());
        return ResponseEntity.ok().build();
    }

    public Optional<User> login(LoginRequestApiDTO loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());

        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        if (userOpt.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            log.info("Login successful for user: {}", loginRequest.getUsername());
            return userOpt;
        }

        log.warn("Login failed for user: {}", loginRequest.getUsername());
        return Optional.empty();
    }

    public void deleteUserById(Long id) {
        log.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new CustomNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);

        log.info("User deleted with id: {}", id);
    }
}