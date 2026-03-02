package org.u2g.codylab.teamboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.u2g.codylab.teamboard.api.AuthApi;
import org.u2g.codylab.teamboard.dto.AuthResponseApiDTO;
import org.u2g.codylab.teamboard.dto.LoginRequestApiDTO;
import org.u2g.codylab.teamboard.dto.RegisterRequestApiDTO;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.service.JwtService;
import org.u2g.codylab.teamboard.service.UserService;

@RestController
public class AuthController implements AuthApi {
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public ResponseEntity<AuthResponseApiDTO> login(LoginRequestApiDTO loginRequestApiDTO) {
        User user = userService.login(loginRequestApiDTO);
        String token = jwtService.generateToken(user.getUsername());
        AuthResponseApiDTO response = new AuthResponseApiDTO();
        response.setToken(token);
        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<Void> register(RegisterRequestApiDTO registerRequestApiDTO) {
        return userService.register(registerRequestApiDTO);
    }
}
