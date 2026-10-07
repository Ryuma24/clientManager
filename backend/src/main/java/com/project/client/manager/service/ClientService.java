package com.project.client.manager.service;

import com.project.client.manager.model.Client;
import com.project.client.manager.model.User;
import com.project.client.manager.repository.ClientRepository;
import com.project.client.manager.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

  private final ClientRepository clientRepository;
  private final UserRepository userRepository;

  public Client createClient(Client client, String userName) {

    client.setUser(
        userRepository
            .findByUsername(userName)
            .orElseThrow(() -> new RuntimeException("User not found")));

    clientRepository.save(client);

    return client;
  }

  public List<Client> getClients(String username) {

    return clientRepository.findByUserUsername(username);
  }

  public void deleteClient(Long clientId, String username) {

    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User Not found!"));

    Client client =
        clientRepository
            .findByIdAndUserId(clientId, user.getId())
            .orElseThrow(() -> new RuntimeException("Client not found"));
    ;

    clientRepository.delete(client);
  }
}
