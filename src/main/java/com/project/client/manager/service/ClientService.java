package com.project.client.manager.service;

import com.project.client.manager.config.PostgresConnection;
import com.project.client.manager.model.Client;
import com.project.client.manager.model.Invoice;
import com.project.client.manager.model.Status;
import com.project.client.manager.repository.ClientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class ClientService {

    private final ClientDao clientDao;

    @Autowired
    PostgresConnection postgresConnection;

    public ClientService(ClientDao clientDao){
        this.clientDao = clientDao;
    }

    public Client addClient(Client client){
        try(Connection connection = postgresConnection.connect()){
            connection.setAutoCommit(false);
            clientDao.insertClient(connection, client);
            return client;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Client> getAllClients(){
        try(Connection connection = postgresConnection.connect()){
            connection.setAutoCommit(false);
            return clientDao.fetchAllClients(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

//    public Client getClientById(Long id){
//        return clientDao.findById(id).orElseThrow(()->new RuntimeException("Client not found"));
//    }
//
//    public void deleteClient(Long id){
//        clientDao.deleteById(id);
//    }
//
//    public Double getTotalClientBalance(Long id) {
//      return clientDao.findAll().stream().flatMap(client -> client.getInvoices()==null?java.util.stream.Stream.empty() : client.getInvoices().stream()).mapToDouble(Invoice::getAmount).sum();
//    }
//
//    public Double getClientOutStandingBalance(Long invoiceId){
//       return clientDao.findAll().stream().flatMap(client -> client.getInvoices()==null?java.util.stream.Stream.empty() : client.getInvoices().stream())
//               .mapToDouble(inv->{
//                   Status s = Status.fromLabel(inv.getStatus());
//                   return Status.OVERDUE.equals(s)? inv.getAmount():0.0;
//               }).sum();
//    }
}
