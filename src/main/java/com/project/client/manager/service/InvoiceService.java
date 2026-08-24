package com.project.client.manager.service;

import com.project.client.manager.model.Client;
import com.project.client.manager.model.Invoice;
import com.project.client.manager.model.PaymentAmountStatus;
import com.project.client.manager.repository.ClientRepository;
import com.project.client.manager.repository.InvoiceRepository;
import com.project.client.manager.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InvoiceService {

  private final InvoiceRepository invoiceRepository;
  private final ClientRepository clientRepository;
  private final UserRepository userRepository;

  public Invoice createInvoice(Invoice invoice, Long clientId, String username) {
    Client client =
        clientRepository
            .findByIdAndUserUsername(clientId, username)
            .orElseThrow(() -> new RuntimeException("Client does not exist"));

    if (invoice.getSubTotal() == null) {
      invoice.setSubTotal(
          invoice.getInvoiceItemList() == null
              ? 0.0
              : invoice.getInvoiceItemList().stream()
                  .mapToDouble(item -> item.getAmount() == null ? 0.0 : item.getAmount())
                  .sum());
    }

    invoice.setClient(client);
    invoice.setUserId(client.getUser().getId());
    invoice.setTotalAmount(java.math.BigDecimal.valueOf(invoice.getSubTotal()));
    invoice.setAmountPaid(java.math.BigDecimal.ZERO);
    invoice.setAmountStatus(PaymentAmountStatus.DORMANT);

    if (invoice.getStatus() == null || invoice.getStatus().isBlank()) {
      invoice.setStatus("PENDING");
    }

    return invoiceRepository.save(invoice);
  }

  public List<Invoice> getInvoicesByClient(Long clientId, String username) {

    clientRepository
        .findByIdAndUserUsername(clientId, username)
        .orElseThrow(() -> new RuntimeException("Client does not exist"));

    return invoiceRepository.findByClient_Id(clientId);
  }

  public List<Invoice> getInvoicesForCurrentUser(String username) {
    String email =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User does not exist"))
            .getEmail();

    Client client =
        clientRepository
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("No client profile found for this account"));

    if (client.getId() == null) {
      throw new RuntimeException("No client profile found for this account");
    }

    return invoiceRepository.findByClient_Id(client.getId());
  }

  public void deleteInvoice(Long invoiceId, String username) {

    Invoice invoice =
        invoiceRepository
            .findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice does not exist"));

    clientRepository
        .findByIdAndUserUsername(invoice.getClient().getId(), username)
        .orElseThrow(() -> new RuntimeException("Client does not exist"));

    invoiceRepository.delete(invoice);
  }
}
