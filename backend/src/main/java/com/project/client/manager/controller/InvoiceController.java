package com.project.client.manager.controller;

import com.project.client.manager.model.Invoice;
import com.project.client.manager.service.InvoiceService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

  private final InvoiceService invoiceService;

  public InvoiceController(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @PostMapping("/create")
  public ResponseEntity<Invoice> createInvoice(
      @RequestBody Invoice invoice, Authentication authentication) {
    if (invoice.getClient() == null || invoice.getClient().getId() == null) {
      throw new RuntimeException("Client is required");
    }

    return ResponseEntity.ok(
        invoiceService.createInvoice(
            invoice, invoice.getClient().getId(), authentication.getName()));
  }

  @GetMapping("/client/{clientId}")
  public ResponseEntity<List<Invoice>> getInvoiceByClientId(
      @PathVariable Long clientId, Authentication authentication) {
    return ResponseEntity.ok(
        invoiceService.getInvoicesByClient(clientId, authentication.getName()));
  }

  @GetMapping("/my")
  public ResponseEntity<List<Invoice>> getMyInvoices(Authentication authentication) {
    return ResponseEntity.ok(invoiceService.getInvoicesForCurrentUser(authentication.getName()));
  }

  //    @Transactional
  //    @PutMapping("/status/{invoiceId}")
  //    public ResponseEntity<Boolean> updateInvoiceStatus(@PathVariable Long invoiceId ,
  // @RequestBody String status){
  //        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(invoiceId,status));
  //    }
}
