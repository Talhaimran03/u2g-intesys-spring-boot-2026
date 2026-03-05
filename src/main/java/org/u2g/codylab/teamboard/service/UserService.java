package org.u2g.codylab.teamboard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.u2g.codylab.teamboard.dto.*;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.exception.CustomAnauthorizedException;
import org.u2g.codylab.teamboard.exception.CustomConflictException;
import org.u2g.codylab.teamboard.exception.CustomIllegalArgumentException;
import org.u2g.codylab.teamboard.exception.CustomNotFoundException;
import org.u2g.codylab.teamboard.mapper.UserMapper;
import org.u2g.codylab.teamboard.repository.UserRepository;

@Slf4j
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public void register(RegisterRequestApiDTO request) {

        log.info("Registering user: {}", request.getUsername());

        if (!request.getUsername().matches("^[a-zA-Z0-9_-]+$")) {
            throw new CustomIllegalArgumentException(
                    "Username contains invalid characters: " + request.getUsername());
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new CustomConflictException(
                    "Username already exists: " + request.getUsername());
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        User user = userMapper.toEntity(request);

        userRepository.save(user);

        log.info("User successfully created: {}", request.getUsername());
    }

    public User login(LoginRequestApiDTO loginRequest) {

        log.info("Login attempt for user: {}", loginRequest.getUsername());

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() ->
                        new CustomAnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {

            log.error("Invalid credentials for {}", loginRequest.getUsername());

            throw new CustomAnauthorizedException("Invalid username or password");
        }

        log.info("User logged in successfully: {}", user.getUsername());

        return user;
    }

    public User getUserById(Long id) {

        log.info("Getting user by id: {}", id);

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new CustomNotFoundException("User not found with id: " + id));
    }

    public void deleteUserById(Long userId) {
        log.info("Deleting user by id: {}", userId);


        String currentUsername = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new CustomNotFoundException("User not found"));


        if (!currentUser.getId().equals(userId)) {
            throw new CustomAnauthorizedException("You can only delete your own account");
        }


        if (!userRepository.existsById(userId)) {
            throw new CustomNotFoundException("User not found with id: " + userId);
        }

        userRepository.deleteById(userId);
        log.info("User deleted: {}", userId);
    }

    public UserResponseApiDTO updatePersonalData(UpdatePersonalDataRequestApiDTO request) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomNotFoundException("User not found"));

        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setEmail(request.getEmail());

        userRepository.save(user);

        return userMapper.toApiDTO(user);
    }

    public UserResponseApiDTO updateUsername(UpdateUsernameRequestApiDTO request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new CustomConflictException("Username already exists");
        }

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomNotFoundException("User not found"));

        user.setUsername(request.getUsername());

        userRepository.save(user);

        return userMapper.toApiDTO(user);
    }

    public void updatePassword(UpdatePasswordRequestApiDTO request) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        log.info("Password updated for user: {}", username);
    }

}