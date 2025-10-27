package com.project.client.manager.service;

import com.project.client.manager.model.Client;
import com.project.client.manager.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository){
        this.clientRepository = clientRepository;
    }

    public Client addClient(Client client){
        return clientRepository.save(client);
    }

    public List<Client> getAllClients(){
        return clientRepository.findAll();
    }

    public Client getClientById(Long id){
        return clientRepository.findById(id).orElseThrow(()->new RuntimeException("Client not found"));
    }

    public void deleteClient(Long id){
        clientRepository.deleteById(id);
    }
}
