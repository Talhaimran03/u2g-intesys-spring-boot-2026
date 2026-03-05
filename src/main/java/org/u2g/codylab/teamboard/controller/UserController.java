package org.u2g.codylab.teamboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.u2g.codylab.teamboard.api.UserApi;
import org.u2g.codylab.teamboard.dto.*;
import org.u2g.codylab.teamboard.service.UserService;

@RestController
public class UserController implements UserApi {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<Void> deleteUserById(Long userId) {

        userService.deleteUserById(userId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Me200ResponseApiDTO> me() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(
                new Me200ResponseApiDTO().username(username)
        );
    }

    @Override
    public ResponseEntity<Void> updatePassword(UpdatePasswordRequestApiDTO request) {

        userService.updatePassword(request);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserResponseApiDTO> updatePersonalData(UpdatePersonalDataRequestApiDTO request) {

        return ResponseEntity.ok(
                userService.updatePersonalData(request)
        );
    }

    @Override
    public ResponseEntity<UserResponseApiDTO> updateUsername(UpdateUsernameRequestApiDTO request) {

        return ResponseEntity.ok(
                userService.updateUsername(request)
        );
    }
}