package org.u2g.codylab.teamboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.u2g.codylab.teamboard.api.UserApi;
import org.u2g.codylab.teamboard.dto.*;
import org.u2g.codylab.teamboard.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponseApiDTO> updatePersonalData(UpdatePersonalDataRequestApiDTO updatePersonalDataRequestApiDTO) {

        return ResponseEntity.ok(userService.updatePersonalData(updatePersonalDataRequestApiDTO));
    }

    @Override
    public ResponseEntity<UserResponseApiDTO> updateUsername(UpdateUsernameRequestApiDTO updateUsernameRequestApiDTO) {

        return ResponseEntity.ok(userService.updateUsername(updateUsernameRequestApiDTO));
    }

    @Override
    public ResponseEntity<Void> updatePassword(UpdatePasswordRequestApiDTO updatePasswordRequestApiDTO) {

        userService.updatePassword(updatePasswordRequestApiDTO);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteUserById(Long userId) {

        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Me200ResponseApiDTO> me() {

        return ResponseEntity.ok(userService.getCurrentUser());
    }
}