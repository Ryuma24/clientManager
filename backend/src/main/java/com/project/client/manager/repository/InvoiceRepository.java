package com.project.client.manager.repository;

import com.project.client.manager.model.Invoice;
import com.project.client.manager.model.InvoiceStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

  List<Invoice> findByUserId(Long userId);

  Optional<Invoice> findByIdAndClientUserUsername(Long id, String username);

  Optional<Invoice> findByIdAndClient_Email(Long id, String email);

  List<Invoice> findByUserIdAndClient_Id(Long userId, Long clientId);

  List<Invoice> findByClient_Id(Long id);

  List<Invoice> findByUserIdAndStatus(Long userId, InvoiceStatus status);

  Optional<Invoice> findByIdAndClient_Id(Long invoiceId, Long clientId);
}
