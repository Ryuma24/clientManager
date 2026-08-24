package com.project.client.manager.controller;

import com.project.client.manager.model.Client;
import com.project.client.manager.service.ClientService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
public class ClientController {
  private final ClientService clientService;

  public ClientController(ClientService clientService) {
    this.clientService = clientService;
  }

  @PostMapping("/create")
  public ResponseEntity<Client> createClient(
      @Valid @RequestBody Client client, Authentication authentication) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(clientService.createClient(client, authentication.getName()));
  }

  @GetMapping("/get")
  public ResponseEntity<List<Client>> getClients(Authentication authentication) {
    return ResponseEntity.ok(clientService.getClients(authentication.getName()));
  }

  @DeleteMapping("/delete/{clientId}")
  public ResponseEntity<String> deleteClient(
      @PathVariable Long clientId, Authentication authentication) {
    clientService.deleteClient(clientId, authentication.getName());
    return ResponseEntity.ok("Client Deleted Successfully");
  }
}
