package com.project.client.manager.controller;

import com.project.client.manager.model.User;
import com.project.client.manager.service.UserAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserAuthService userAuthService;

    public AuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signUpUser(@RequestBody User user){
        return ResponseEntity.ok(userAuthService.signUpUser(user));
    }

    @PostMapping("/signin")
    public ResponseEntity<User> signInUser(@RequestBody User user){
        return ResponseEntity.ok(userAuthService.signInUser(user));
    }


}
