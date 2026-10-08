package com.project.client.manager.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.client.manager.dto.RegisterRequest;
import com.project.client.manager.model.Role;
import com.project.client.manager.model.User;
import com.project.client.manager.repository.ClientRepository;
import com.project.client.manager.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private ClientRepository clientRepository;
  @Mock private UserService userService;

  @InjectMocks private AuthService authService;

  @Test
  void registerAccount_allowsClientAccountBeforeProfileExists() {
    RegisterRequest request =
        new RegisterRequest("client-one", "client@example.com", "Password1!", Role.CLIENT);
    User createdUser = User.builder().id(12L).username("client-one").role(Role.CLIENT).build();

    when(clientRepository.findByEmailIgnoreCase("client@example.com")).thenReturn(Optional.empty());
    when(userService.insertUser(any(User.class))).thenReturn(createdUser);

    User result = authService.registerAccount(request);

    assertSame(createdUser, result);
    verify(userService).insertUser(any(User.class));
    verify(clientRepository, never()).save(any());
  }
}
