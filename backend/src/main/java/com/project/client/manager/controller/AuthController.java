package com.project.client.manager.controller;

import com.project.client.manager.dto.AuthResponse;
import com.project.client.manager.dto.LoginRequest;
import com.project.client.manager.dto.RegisterRequest;
import com.project.client.manager.model.User;
import com.project.client.manager.security.JwtService;
import com.project.client.manager.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;
  private final JwtService jwtService;

  @PostMapping("/login")
  public ResponseEntity<?> userLogin(@Valid @RequestBody LoginRequest request) {
    try {

      User existingUser =
          authService.authenticate(request).orElseThrow(() -> new RuntimeException(""));

      String token =
          jwtService.generateToken(
              request.getUsername(),
              Map.of(
                  "role", existingUser.getRole().name(),
                  "username", existingUser.getUsername(),
                  "email", existingUser.getEmail()));

      return ResponseEntity.ok(
          AuthResponse.builder()
              .username(request.getUsername())
              .role(existingUser.getRole())
              .token(token)
              .build());

    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "Invalid username or password"));
    }
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    try {
      User createdUser = authService.registerAccount(request);
      String token =
          jwtService.generateToken(
              createdUser.getUsername(),
              Map.of(
                  "role", createdUser.getRole(),
                  "email", createdUser.getEmail(),
                  "username", createdUser.getUsername()));

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(
              AuthResponse.builder()
                  .token(token)
                  .username(createdUser.getUsername())
                  .email(createdUser.getEmail())
                  .role(createdUser.getRole())
                  .build());

    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest().body(AuthResponse.builder().build());
    }
  }
}
