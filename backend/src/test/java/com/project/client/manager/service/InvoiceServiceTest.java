package com.project.client.manager.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.client.manager.model.Invoice;
import com.project.client.manager.model.User;
import com.project.client.manager.repository.ClientRepository;
import com.project.client.manager.repository.InvoiceRepository;
import com.project.client.manager.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

  @Mock private InvoiceRepository invoiceRepository;
  @Mock private ClientRepository clientRepository;
  @Mock private UserRepository userRepository;

  @InjectMocks private InvoiceService invoiceService;

  @Test
  void getInvoicesForCurrentUser_usesClientMembershipByUserId() {
    User clientAccount = new User();
    clientAccount.setId(42L);
    clientAccount.setUsername("client-login");
    List<Invoice> invoices = List.of(new Invoice());

    when(userRepository.findByUsername("client-login")).thenReturn(Optional.of(clientAccount));
    when(invoiceRepository.findByClient_Users_Id(42L)).thenReturn(invoices);

    List<Invoice> result = invoiceService.getInvoicesForCurrentUser("client-login");

    assertSame(invoices, result);
    verify(invoiceRepository).findByClient_Users_Id(42L);
  }
}
