package com.project.client.manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.project.client.manager.model.Role;
import com.project.client.manager.model.User;
import com.project.client.manager.repository.UserRepository;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {

  // @Test
  void inserUserShouldEncodePasswordAndPersistUserDetails() {
    UserRepository userRepository = mock(UserRepository.class);
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    when(passwordEncoder.encode("StrongPass1!")).thenReturn("encoded-password");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    UserService userService = new UserService(userRepository, passwordEncoder);
    User input =
        User.builder()
            .username("alice")
            .password("StrongPass1!")
            .email("alice@example.com")
            .role(Role.USER)
            .build();

    userService.insertUser(input);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());

    User savedUser = userCaptor.getValue();
    assertThat(savedUser.getUsername()).isEqualTo("alice");
    assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
    assertThat(savedUser.getEmail()).isEqualTo("alice@example.com");
    assertThat(savedUser.getRole()).isEqualTo(Role.USER);
    assertThat(savedUser.getEnabled()).isTrue();
  }
}
