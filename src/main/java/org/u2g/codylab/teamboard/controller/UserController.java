package org.u2g.codylab.teamboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.service.ProjectService;
import org.u2g.codylab.teamboard.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    List<User> usersList = new ArrayList<>();
   private final UserService userService;
     public UserController(UserService userService) {
         this.userService = userService;
     }

     @GetMapping
    public List<User> getUsers(){
         return userService.getAllUsers();
     }
     @GetMapping("/{id}")
    public User getOneUser(@PathVariable Long id) throws ResponseStatusException {
        User user = userService.getOneUser(id);
        if(user == null){
            throw new  ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
     }

     @PostMapping
    public List<User> createUser(@RequestBody User user) {
         usersList.add(user);
         return new ArrayList<>(usersList);
     }
 }
