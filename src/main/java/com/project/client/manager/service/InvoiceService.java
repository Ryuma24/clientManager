package com.project.client.manager.service;

import com.project.client.manager.model.Invoice;

import java.util.List;


public interface InvoiceService {
    Invoice createInvoice(Invoice invoice);
    List<Invoice> findClientById(Long clientId);
}
