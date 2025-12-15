package com.project.client.manager;

import com.project.client.manager.model.Invoice;

import java.util.List;

public class postgressDataIngestion {
    public void ingestData(int id, String name, String email , String phoneNumber , List<Invoice> invoices) {
        String insertQuery = "INSERT INTO clients (id, name, email, phone ,) VALUES (?, ?, ?, ?)";
    }
}
