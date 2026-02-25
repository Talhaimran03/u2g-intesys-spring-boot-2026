package org.u2g.codylab.teamboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.u2g.codylab.teamboard.api.UserApi;
import org.u2g.codylab.teamboard.dto.Login200ResponseApiDTO;
import org.u2g.codylab.teamboard.dto.LoginRequestApiDTO;
import org.u2g.codylab.teamboard.dto.Me200ResponseApiDTO;
import org.u2g.codylab.teamboard.dto.UserApiDTO;
import org.u2g.codylab.teamboard.mapper.UserMapper;
import org.u2g.codylab.teamboard.service.JwtService;
import org.u2g.codylab.teamboard.service.TokenBlacklistService;
import org.u2g.codylab.teamboard.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class UserController implements UserApi {
    private final UserService userService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserMapper userMapper;  // ← AJOUTÉ

    public UserController(UserService userService,
                          JwtService jwtService,
                          TokenBlacklistService tokenBlacklistService,
                          UserMapper userMapper) {  // ← AJOUTÉ
        this.userService = userService;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.userMapper = userMapper;  // ← AJOUTÉ
    }

    @Override
    public ResponseEntity<Login200ResponseApiDTO> login(LoginRequestApiDTO loginRequestApiDTO) {
        return userService.login(loginRequestApiDTO)
                .map(u ->{
                    String token = jwtService.generateToken(loginRequestApiDTO.getUsername());
                    Login200ResponseApiDTO response = new Login200ResponseApiDTO();
                    response.setToken(token);
                    return ResponseEntity.ok(response);
                }).orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Override
    public ResponseEntity<Void> register(UserApiDTO userApiDTO) {
        return userService.register(userApiDTO);
    }

    @Override
    public ResponseEntity<Void> deleteUserById(Long id) {
        boolean deleted = userService.deleteUserById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<UserApiDTO> getUserById(Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(userMapper.toApiDTO(user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Override
    public ResponseEntity<Me200ResponseApiDTO> me() {

        Me200ResponseApiDTO username = userService.me();

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(username);
    }
}
