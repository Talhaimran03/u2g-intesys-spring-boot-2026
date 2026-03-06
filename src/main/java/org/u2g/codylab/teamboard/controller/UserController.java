package org.u2g.codylab.teamboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.u2g.codylab.teamboard.api.UserApi;
import org.u2g.codylab.teamboard.dto.*;
import org.u2g.codylab.teamboard.service.UserService;

import java.util.List;

@RestController
public class UserController implements UserApi {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<UserResponseApiDTO> updatePersonalDataUser(UpdatePersonalDataRequestApiDTO updatePersonalDataRequestApiDTO) {
        return ResponseEntity.ok(userService.updatePersonalDataUser(updatePersonalDataRequestApiDTO));
    }

    @Override
    public ResponseEntity<UserResponseApiDTO> updateUsername(UpdateUsernameRequestApiDTO updateUsernameRequestApiDTO) {
        return ResponseEntity.ok(userService.updateUsernameData(updateUsernameRequestApiDTO));
    }

    @Override
    public ResponseEntity<Void> updatePassword(UpdatePasswordRequestApiDTO updatePasswordRequestApiDTO) {
        userService.updateUserPassword(updatePasswordRequestApiDTO);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteUserById(Long id){
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Me200ResponseApiDTO> me(){
        return ResponseEntity.ok(userService.me());
    }
}
