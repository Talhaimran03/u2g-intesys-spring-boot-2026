package org.u2g.codylab.teamboard.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.u2g.codylab.teamboard.dto.LoginRequestApiDTO;
import org.u2g.codylab.teamboard.dto.Me200ResponseApiDTO;
import org.u2g.codylab.teamboard.dto.UserApiDTO;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.mapper.UserMapper;
import org.u2g.codylab.teamboard.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final HttpServletRequest httpServletRequest;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, HttpServletRequest httpServletRequest, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.httpServletRequest = httpServletRequest;
        this.jwtService = jwtService;
    }

    public ResponseEntity<Void> register(UserApiDTO user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(409).build();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User userEntity = userMapper.toEntity(user);
        userRepository.save(userEntity);

        return ResponseEntity.ok().build();
    }
    public Optional<User> login(LoginRequestApiDTO loginRequest) {
       Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        if (userOpt.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public boolean deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Me200ResponseApiDTO me() {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        return new Me200ResponseApiDTO().username(username);
    }
}
