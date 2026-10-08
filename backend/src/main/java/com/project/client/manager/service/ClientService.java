package com.project.client.manager.service;

import com.project.client.manager.model.Client;
import com.project.client.manager.model.User;
import com.project.client.manager.repository.ClientRepository;
import com.project.client.manager.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {

  private final ClientRepository clientRepository;
  private final UserRepository userRepository;

  @Transactional
  public Client createClient(Client client, String userName) {

    User owner =
        userRepository
            .findByUsername(userName)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Client savedClient = clientRepository.save(client);

    owner.getClients().add(savedClient);
    savedClient.getUsers().add(owner);

    return savedClient;
  }

  @Transactional(readOnly = true)
  public List<Client> getClients(String username) {
    return clientRepository.findByUsers_Username(username);
  }

  // we don't need to save user into user repository , hibernate is managing that
  @Transactional
  public void deleteClient(Long clientId, String username) {

    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Client client =
        clientRepository
            .findByIdAndUsers_Username(clientId, username)
            .orElseThrow(() -> new RuntimeException("Client not found"));

    user.getClients().remove(client);
    client.getUsers().remove(user);

    if (client.getUsers().isEmpty()) {
      clientRepository.delete(client);
    }
  }
}
