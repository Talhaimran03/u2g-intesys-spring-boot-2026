package org.u2g.codylab.teamboard.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.u2g.codylab.teamboard.entity.LoginRequest;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.exception.UsernameAlreadyExistsException;
import org.u2g.codylab.teamboard.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }
    public  Optional<User> login(LoginRequest loginRequest) {
       Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        if (userOpt.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }
    public void deleteuser(Long id) {
        if(!userRepository.existsById(id)) {
            throw new RuntimeException("User not found!");
        }
         userRepository.deleteById(id);
    }
}
