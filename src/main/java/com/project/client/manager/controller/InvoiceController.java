package com.project.client.manager.controller;

import com.project.client.manager.model.Invoice;
import com.project.client.manager.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity createInvoice(@RequestBody Invoice invoice){
        return ResponseEntity.ok(invoiceService.createInvoice(invoice));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Invoice>> getInvoiceByClientId(@PathVariable Long clientId){
        return ResponseEntity.ok(invoiceService.findClientById(clientId));
    }
}
