package org.u2g.codylab.teamboard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import org.u2g.codylab.teamboard.util.AuthUtil;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Service
public class UserService {

    // Username validation: only letters, numbers, and underscores are allowed
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]+$";

    // Password validation: at least 8 characters, at least one uppercase letter, one lowercase letter, one number, and one special character
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

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

    public ResponseEntity<Void> register(RegisterRequestApiDTO registerRequestApiDTO) {

        log.info("Registering user: {}", registerRequestApiDTO.getUsername());
        String username = registerRequestApiDTO.getUsername();
        String password = registerRequestApiDTO.getPassword();
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new CustomIllegalArgumentException("Username and password are required");
        }

        username = username.trim();
        if (!username.matches(USERNAME_REGEX)) {
            throw new CustomIllegalArgumentException("Username must not contain special characters");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new CustomConflictException("Username already exists");
        }

        if (!password.matches(PASSWORD_REGEX)) {
            throw new CustomIllegalArgumentException("Password is too weak: must be at least 8 characters, contain upper and lower case, a number and a special character");
        }
        registerRequestApiDTO.setUsername(username);
        registerRequestApiDTO.setPassword(passwordEncoder.encode(password));
        User userEntity = userMapper.toEntity(registerRequestApiDTO);
        userRepository.save(userEntity);
        log.info("User registered: {}", username);
        return ResponseEntity.ok().build();
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

        return userMapper.toResponseApiDTO(user);
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

        return userMapper.toResponseApiDTO(user);
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

    public Me200ResponseApiDTO me() {
        User user = AuthUtil.getLoggedUser(userRepository);
        return new Me200ResponseApiDTO().username(user.getUsername());
    }

    public List<UserApiDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toApiDTO).toList();
    }
}