package com.project.client.manager.service;

import com.project.client.manager.config.PostgresConnection;
import com.project.client.manager.model.Invoice;
import com.project.client.manager.repository.InvoiceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    InvoiceDao invoiceRepository;

    @Autowired
    PostgresConnection postgresConnection;




    public List<Invoice> createInvoice(Long clientId, List<Invoice> invoices) {
        try(Connection connection = postgresConnection.connect()){
            return invoiceRepository.insertInvoice(connection, clientId, invoices);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public List<Invoice> findClientById(Long clientId) {
        return invoiceRepository.getInvoicesByClientId(clientId);
    }
}
