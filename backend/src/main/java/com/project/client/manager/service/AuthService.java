package com.project.client.manager.service;

import com.project.client.manager.dto.LoginRequest;
import com.project.client.manager.dto.RegisterRequest;
import com.project.client.manager.model.Role;
import com.project.client.manager.model.User;
import com.project.client.manager.repository.UserRepository;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;

  public AuthService(
      UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
  }

  @Transactional
  public User registerUser(RegisterRequest request) {
    Role selectedRole = request.getRole() != null ? request.getRole() : Role.USER;

    User user =
        User.builder()
            .username(request.getUsername())
            .password(request.getPassword())
            .email(request.getEmail())
            .role(selectedRole)
            .build();

    return userService.insertUser(user);
  }

  @Transactional(readOnly = true)
  public Optional<User> loginUser(LoginRequest request) {

    Optional<User> existingUser =
        Optional.of(
            userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found")));

    if (request.getRole() != null && !request.getRole().isBlank()) {
      String requestedRole = request.getRole().trim().toUpperCase();
      String actualRole = existingUser.get().getRole().name();
      if (!requestedRole.equals(actualRole)) {
        throw new RuntimeException("Role mismatch");
      }
    }

    if (passwordEncoder.matches(request.getPassword(), existingUser.get().getPassword())) {
      return existingUser;
    }

    throw new RuntimeException("Incorrect Password");
  }
}
