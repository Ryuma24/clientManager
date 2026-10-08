package com.project.client.manager.repository;

import com.project.client.manager.model.Invoice;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

  Optional<Invoice> findByIdAndClient_Users_Id(Long id, Long userId);

  List<Invoice> findByClient_Users_Id(Long userId);

  List<Invoice> findByClient_Id(Long id);

  Optional<Invoice> findByIdAndClient_Id(Long invoiceId, Long clientId);
}
