package com.project.client.manager.service;

import com.project.client.manager.model.Role;
import com.project.client.manager.model.User;
import com.project.client.manager.repository.UserRepository;
import java.util.List;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public User insertUser(User user) {
    User savedUser =
        User.builder()
            .username(user.getUsername())
            .password(passwordEncoder.encode(user.getPassword()))
            .email(user.getEmail())
            .role(user.getRole() != null ? user.getRole() : Role.USER)
            .enabled(true)
            .build();

    return userRepository.save(savedUser);
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User findByUserName(String username) {

    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found : " + username));
  }
}
