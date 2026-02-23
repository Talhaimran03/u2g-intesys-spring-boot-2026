package org.u2g.codylab.teamboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")

public class UserController {
    List<User> users;

    private  final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {return  userService.getUserById(id);}

    @GetMapping
    public List<User> getAllUsers() {return  userService.getAllUsers();}
}
