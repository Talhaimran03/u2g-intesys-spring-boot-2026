package org.u2g.codylab.teamboard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.u2g.codylab.teamboard.dto.LoginRequestApiDTO;
import org.u2g.codylab.teamboard.dto.RegisterRequestApiDTO;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.exception.CustomAnauthorizedException;
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
        log.info("Registering user: {}", registerRequestApiDTO);
        if (userRepository.findByUsername(registerRequestApiDTO.getUsername()).isPresent()) {
            log.info("Username is already in use: {}", registerRequestApiDTO.getUsername());
            throw new CustomConflictException("Username is already in use: " + registerRequestApiDTO.getUsername());
        }
        registerRequestApiDTO.setPassword(passwordEncoder.encode(registerRequestApiDTO.getPassword()));
        log.info("Password encoded: {}", registerRequestApiDTO.getPassword());

        User userEntity = userMapper.toEntity(registerRequestApiDTO);
        userRepository.save(userEntity);
        log.info("User created: {}", registerRequestApiDTO);

        return ResponseEntity.ok().build();
    }
    public User login(LoginRequestApiDTO loginRequest) {
        log.info("Login attempt for user: {}",
                loginRequest.getUsername());
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new CustomAnauthorizedException("Invalid username or password"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.error("Unauthorized: invalid credentials for {}", loginRequest.getUsername());
            throw new CustomAnauthorizedException("Invalid username or password");
        }
        log.info("User logged in successfully: {}", user.getUsername()); return user; }

    public User getUserById(Long id) {
        log.info("Getting user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("User not found with id: " + id));
    }


    public void deleteUserById(Long id) {
        log.info("Deleting user by id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new CustomNotFoundException("User not found with id: " + id);
        }
        log.info("User deleted: {}", id);
        userRepository.deleteById(id);
    }


}
