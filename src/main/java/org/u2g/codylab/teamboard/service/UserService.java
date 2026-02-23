package org.u2g.codylab.teamboard.service;

import org.springframework.stereotype.Service;
import org.u2g.codylab.teamboard.entity.User;

import java.util.List;

@Service

public class UserService {
    private final List<User> users = List.of(
            new User(1L, "Alice", "Dupont"),
            new User(2L, "Bob", "Martin")
    );
    public User getUserById(Long id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }
    public List<User> getAllUsers() {
        return users;
    }
}
