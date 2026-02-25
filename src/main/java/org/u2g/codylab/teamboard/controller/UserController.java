package org.u2g.codylab.teamboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.u2g.codylab.teamboard.api.UserApi;
import org.u2g.codylab.teamboard.dto.LoginRequestApiDTO;
import org.u2g.codylab.teamboard.dto.UserApiDTO;
import org.u2g.codylab.teamboard.entity.LoginRequest;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.service.UserService;

@RestController
public class UserController implements UserApi {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<Void> login(LoginRequestApiDTO loginRequestApiDTO) {
        if (userService.login(loginRequestApiDTO).isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
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
}

