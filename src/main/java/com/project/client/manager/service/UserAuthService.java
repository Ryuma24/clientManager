package com.project.client.manager.service;

import com.project.client.manager.model.User;
import com.project.client.manager.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import com.project.client.manager.config.SecurityConfig;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signUpUser(User user){
        String passwordHash = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(passwordHash);
        user.setCreatedAtTs(LocalDateTime.now());
        user.setUpdatedAtTs(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User signInUser(User user){
        var existingUser = userRepository.findByEmail(user.getEmail());
        boolean matches = passwordEncoder.matches(user.getPasswordHash(),existingUser.getPasswordHash());
        if(!matches) {
            throw new RuntimeException("Invalid Credentials");
        }
        return existingUser;
    }

}
