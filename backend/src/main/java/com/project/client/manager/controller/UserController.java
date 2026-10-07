package com.project.client.manager.controller;

import com.project.client.manager.model.User;
import com.project.client.manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  public User getCurrentUser(Authentication authentication) {
    String username = authentication.getName();
    return userService.findByUserName(username);
  }
}
