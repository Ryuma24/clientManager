package com.project.client.manager.service;

import java.util.List;

import com.project.client.manager.model.Client;
import com.project.client.manager.model.Invoice;

public class PaymentService {

    private InvoiceService invoiceService;

    public boolean makeRazorPayPayment(Client client, Invoice invoice, Double amount) {



    }

    public boolean makePayPalPayment(Client client, Invoice invoice, Double amount){



    }


    private Boolean updateInvoice(Client client, Invoice invoice, Double amount) {
        List<Invoice> listOfInvoices = invoiceService.findClientById(client.getId());
        double balance;
        Long id = invoice.getId();
        if (listOfInvoices.contains(invoice)) {
            balance = invoice.getAmount() - amount;
            invoice.setAmount(balance);
            if (Double.compare(balance, 0.0) == 0) {
                invoice.setStatus("PAID");
                return invoiceService.updateInvoice(id, "PAID", amount);
            } else if (amount != 0.0) {
                invoice.setAmount(balance);
                invoice.setStatus("PARTIAL");
                return invoiceService.updateInvoice(id, "PARTIAL", amount);
            }
        }


    }

}