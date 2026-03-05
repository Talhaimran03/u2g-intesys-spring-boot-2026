package org.u2g.codylab.teamboard.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.exception.CustomAnauthorizedException;
import org.u2g.codylab.teamboard.repository.UserRepository;

import java.util.Objects;

public class AuthUtil {

    public static User getLoggedUser(UserRepository userRepository) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = Objects.requireNonNull(authentication).getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAnauthorizedException("User not found with username: " + username));
    }
}

