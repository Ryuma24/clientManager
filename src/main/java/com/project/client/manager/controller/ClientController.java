package com.project.client.manager.controller;

import com.project.client.manager.model.Client;
import com.project.client.manager.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService){
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client){
        Client savedClient = clientService.addClient(client);
        var location = URI.create("/clients/"+savedClient.getId());
        return ResponseEntity.created(location).body(savedClient);
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients(){
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id){
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Long id){
        clientService.deleteClient(id);
        return ResponseEntity.ok("Client Deleted Successfully");
    }

    @GetMapping("/balance/{id}")
    public ResponseEntity<Double> getTotalClientBalance(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getTotalClientBalance(id));
    }

    @GetMapping("/outstanding/{id}")
    public ResponseEntity<Double> getClientOutStandingBalance(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientOutStandingBalance(id));
    }
}
