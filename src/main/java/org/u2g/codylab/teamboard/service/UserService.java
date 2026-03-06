package org.u2g.codylab.teamboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.u2g.codylab.teamboard.dto.*;
import org.u2g.codylab.teamboard.entity.User;
import org.u2g.codylab.teamboard.exception.CustomConflictException;
import org.u2g.codylab.teamboard.exception.CustomIllegalArgumentException;
import org.u2g.codylab.teamboard.exception.CustomNotFoundException;
import org.u2g.codylab.teamboard.mapper.UserMapper;
import org.u2g.codylab.teamboard.repository.UserRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]+$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    public Me200ResponseApiDTO getCurrentUser() {
        User user = getAuthenticatedUser();
        Me200ResponseApiDTO response = new Me200ResponseApiDTO();
        response.setUsername(user.getUsername());
        return response;
    }


    public UserResponseApiDTO updatePersonalData(UpdatePersonalDataRequestApiDTO request) {
        User user = getAuthenticatedUser();

        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setEmail(request.getEmail());

        log.info("Mise à jour des données personnelles pour : {}", user.getUsername());
        return userMapper.toDto(userRepository.save(user));
    }


    public UserResponseApiDTO updateUsername(UpdateUsernameRequestApiDTO request) {
        User user = getAuthenticatedUser();
        String newUsername = request.getUsername().trim();


        if (!newUsername.matches(USERNAME_REGEX)) {
            throw new CustomIllegalArgumentException("Le format du pseudo est invalide");
        }


        if (userRepository.findByUsername(newUsername).isPresent()) {
            throw new CustomConflictException("Ce pseudo est déjà utilisé");
        }

        user.setUsername(newUsername);
        return userMapper.toDto(userRepository.save(user));
    }


    public void updatePassword(UpdatePasswordRequestApiDTO request) {
        User user = getAuthenticatedUser();
        String newPassword = request.getPassword();


        if (!newPassword.matches(PASSWORD_REGEX)) {
            throw new CustomIllegalArgumentException("Le mot de passe est trop faible");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Mot de passe modifié avec succès pour : {}", user.getUsername());
    }


    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new CustomNotFoundException("Utilisateur non trouvé avec l'ID : " + id);
        }
        userRepository.deleteById(id);
        log.info("Utilisateur supprimé (ID: {})", id);
    }


    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomNotFoundException("Utilisateur non trouvé dans le contexte de sécurité"));
    }
}