package org.u2g.codylab.teamboard.service;

import org.springframework.stereotype.Service;
import org.u2g.codylab.teamboard.entity.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    public List<User> getAllUsers() {
        return List.of(new User(1L,"Rivaldo","Stephane"));

    }

    public User getOneUser(long id){
        List<User> users = getAllUsers();
        Optional<User> user = users.stream().filter(ur -> ur.getId() == id).findFirst();
        return user.orElse(null);
    }

}
