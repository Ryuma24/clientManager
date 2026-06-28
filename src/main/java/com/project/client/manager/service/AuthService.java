package com.project.client.manager.service;

import com.project.client.manager.dto.LoginRequest;
import com.project.client.manager.dto.RegisterRequest;
import com.project.client.manager.model.Role;
import com.project.client.manager.model.User;
import com.project.client.manager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .role(Role.USER)
                .build();

        return userService.insertUser(user);
    }


    @Transactional(readOnly = true)
    public Optional<User> loginUser(LoginRequest request) {

        Optional<User> existingUser = Optional.of(userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found")));

        if(passwordEncoder.matches(request.getPassword(), existingUser.get().getPassword())){
            return existingUser;
        }

        throw new RuntimeException("Incorrect Password");

    }

}
