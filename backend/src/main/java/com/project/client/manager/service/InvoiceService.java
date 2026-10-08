package com.project.client.manager.service;

import com.project.client.manager.model.Client;
import com.project.client.manager.model.Invoice;
import com.project.client.manager.model.PaymentAmountStatus;
import com.project.client.manager.repository.ClientRepository;
import com.project.client.manager.repository.InvoiceRepository;
import com.project.client.manager.repository.UserRepository;
import java.math.BigDecimal;
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
            .findByIdAndUsers_Username(clientId, username)
            .orElseThrow(() -> new RuntimeException("Client does not exist"));
    var creator =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User does not exist"));

    if (invoice.getSubTotal() == null) {
      invoice.setSubTotal(
          invoice.getInvoiceItemList() == null
              ? BigDecimal.ZERO
              : invoice.getInvoiceItemList().stream()
                  .map(
                      item -> BigDecimal.valueOf(item.getAmount() == null ? 0.0 : item.getAmount()))
                  .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    invoice.setClient(client);
    invoice.setCreatedBy(creator);
    invoice.setTotalAmount(invoice.getSubTotal());
    invoice.setAmountPaid(BigDecimal.ZERO);
    invoice.setAmountStatus(PaymentAmountStatus.DORMANT);
    invoice.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
    invoice.setUpdatedAt(invoice.getCreatedAt());

    if (invoice.getStatus() == null || invoice.getStatus().isBlank()) {
      invoice.setStatus("PENDING");
    }

    return invoiceRepository.save(invoice);
  }

  public List<Invoice> getInvoicesByClient(Long clientId, String username) {

    clientRepository
        .findByIdAndUsers_Username(clientId, username)
        .orElseThrow(() -> new RuntimeException("Client does not exist"));

    return invoiceRepository.findByClient_Id(clientId);
  }

  public List<Invoice> getInvoicesForCurrentUser(String username) {
    Long userId =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User does not exist"))
            .getId();
    return invoiceRepository.findByClient_Users_Id(userId);
  }

  public void deleteInvoice(Long invoiceId, String username) {

    Invoice invoice =
        invoiceRepository
            .findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice does not exist"));

    clientRepository
        .findByIdAndUsers_Username(invoice.getClient().getId(), username)
        .orElseThrow(() -> new RuntimeException("Client does not exist"));

    invoiceRepository.delete(invoice);
  }
}
