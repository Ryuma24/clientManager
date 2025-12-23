package com.project.client.manager.controller;

import com.project.client.manager.model.Invoice;
import com.project.client.manager.service.InvoiceService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/insert/{clientId}")
    public ResponseEntity<List<Invoice>> createInvoice(@PathVariable Long clientId ,@RequestBody List<Invoice> invoices){
        return ResponseEntity.ok(invoiceService.createInvoice(clientId , invoices));


    }

    @GetMapping("/all")
    public ResponseEntity<List<Invoice>> getAllInvoices() throws SQLException {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Invoice>> getInvoiceByClientId(@PathVariable Long clientId){
        return ResponseEntity.ok(invoiceService.findClientById(clientId));
    }

    @Transactional
    @PutMapping("/status/{invoiceId}")
    public ResponseEntity<Boolean> updateInvoiceStatus(@PathVariable Long invoiceId , @RequestBody String status){
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(invoiceId,status));
    }
}
