package org.u2g.codylab.teamboard.service;

import org.springframework.stereotype.Service;
import org.u2g.codylab.teamboard.entity.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final List<User> users = new ArrayList<>();

    public UserService() {
        users.add(new User(1L, "Alice", "Smith"));
        users.add(new User(2L, "Bob", "Johnson"));
        users.add(new User(3L, "Charlie", "Williams"));
    }

    public User getUserById(Long id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
