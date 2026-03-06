package org.u2g.codylab.teamboard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.u2g.codylab.teamboard.dto.*;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.exception.CustomConflictException;
import org.u2g.codylab.teamboard.exception.CustomForbiddenException;
import org.u2g.codylab.teamboard.exception.CustomIllegalArgumentException;
import org.u2g.codylab.teamboard.exception.CustomNotFoundException;
import org.u2g.codylab.teamboard.mapper.UserMapper;
import org.u2g.codylab.teamboard.repository.UserRepository;
import org.u2g.codylab.teamboard.util.AuthUtil;

import java.util.List;
import java.util.Optional;

import static java.rmi.server.LogStream.log;

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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public ResponseEntity<Void> register(RegisterRequestApiDTO registerRequestApiDTO) {

        UserService.log.info("Registering user: {}", registerRequestApiDTO.getUsername());
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
        UserService.log.info("User registered: {}", username);
        return ResponseEntity.ok().build();
    }

    public Optional<User> login(LoginRequestApiDTO loginRequest) {
        UserService.log.info("Login attempt for user: {}", loginRequest.getUsername());
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        if (userOpt.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            UserService.log.info("Login successful for user: {}", loginRequest.getUsername());
            return userOpt;
        }
        UserService.log.warn("Login failed for user: {}", loginRequest.getUsername());
        return Optional.empty();
    }

           /*
    Update Personal data for user
     */

    public UserResponseApiDTO updatePersonalDataUser(UpdatePersonalDataRequestApiDTO dto) {
        User user = AuthUtil.getLoggedUser(userRepository);
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        userRepository.save(user);
        return userMapper.toResponseApiDTO(user);
    }

    /*
 Update User-data
  */
    public UserResponseApiDTO updateUsernameData(UpdateUsernameRequestApiDTO dto) {
        log.info("check user is logging");
        User user = AuthUtil.getLoggedUser(userRepository);
        if (!user.getUsername().equals(dto.getUsername()) && userRepository.findByUsername(dto.getUsername()).isPresent()) {
            log.error("Username already exist");
            throw new CustomConflictException("Username already exist");
        }
        log.info("updated username for user");
        user.setUsername(dto.getUsername());
        userRepository.save(user);
        return userMapper.toResponseApiDTO(user);
    }

        /*
    Update Password for user
     */

    public void updateUserPassword(UpdatePasswordRequestApiDTO dto) {
        log.info("check user is logging");
        User user = AuthUtil.getLoggedUser(userRepository);
        if (!dto.getPassword().matches(PASSWORD_REGEX)) {
            log.error("password not match");
            throw new CustomIllegalArgumentException("Password not match");
        }
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
    }

    /*
    Delete User by Id
     */
    public boolean deleteUserById(Long id) {
        UserService.log.info("Deleting user with id: {}", id);
        User user = AuthUtil.getLoggedUser(userRepository);

        if (!user.getId().equals(id)) {
            log.error("User not authorized to delete this user");
            throw new CustomForbiddenException("User not authorized to delete this user");
        }

        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            UserService.log.info("User deleted: {}", id);
            return true;
        }
        UserService.log.warn("User not found for delete: {}", id);
        throw new CustomIllegalArgumentException("User not found");
    }

           /*
    Return username for the user is logging
     */

    public Me200ResponseApiDTO me(){
        User user = AuthUtil.getLoggedUser((userRepository));
        return new Me200ResponseApiDTO().username(user.getUsername());
    }



}
