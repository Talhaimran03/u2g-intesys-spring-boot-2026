package org.u2g.codylab.teamboard;

import org.springframework.web.bind.annotation.*;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
