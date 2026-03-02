package org.u2g.codylab.teamboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.u2g.codylab.teamboard.api.UserApi;
import org.u2g.codylab.teamboard.dto.*;

@RestController
public class UserController implements UserApi {

    @Override
    public ResponseEntity<Void> deleteUserById(Long userId) {
        return UserApi.super.deleteUserById(userId);
    }

    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Me200ResponseApiDTO> me() {
        return UserApi.super.me();
    }

    @Override
    public ResponseEntity<Void> updatePassword(UpdatePasswordRequestApiDTO updatePasswordRequestApiDTO) {
        return UserApi.super.updatePassword(updatePasswordRequestApiDTO);
    }

    @Override
    public ResponseEntity<UserResponseApiDTO> updatePersonalData(UpdatePersonalDataRequestApiDTO updatePersonalDataRequestApiDTO) {
        return UserApi.super.updatePersonalData(updatePersonalDataRequestApiDTO);
    }

    @Override
    public ResponseEntity<UserResponseApiDTO> updateUsername(UpdateUsernameRequestApiDTO updateUsernameRequestApiDTO) {
        return UserApi.super.updateUsername(updateUsernameRequestApiDTO);
    }
}
