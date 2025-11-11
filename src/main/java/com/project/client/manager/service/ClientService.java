package com.project.client.manager.service;

import com.project.client.manager.model.Client;
import com.project.client.manager.model.Invoice;
import com.project.client.manager.model.Status;
import com.project.client.manager.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    public Double getTotalClientBalance(Long id) {
      return clientRepository.findAll().stream().flatMap(client -> client.getInvoices()==null?java.util.stream.Stream.empty() : client.getInvoices().stream()).mapToDouble(Invoice::getAmount).sum();
    }

    public Double getClientOutStandingBalance(Long invoiceId){
       return clientRepository.findAll().stream().flatMap(client -> client.getInvoices()==null?java.util.stream.Stream.empty() : client.getInvoices().stream())
               .mapToDouble(inv->{
                   Status s = Status.fromLabel(inv.getStatus());
                   return Status.OVERDUE.equals(s)? inv.getAmount():0.0;
               }).sum();
    }
}
