package com.project.client.manager.repository;

import com.project.client.manager.model.Invoice;
import com.project.client.manager.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByUserId(Long userId);

    Optional<Invoice> findByIdAndUserId(Long id, Long userId);

    List<Invoice> findByUserIdAndClientId(Long userId, Long clientId);

    List<Invoice> findByClientId(Long id);

    List<Invoice> findByUserIdAndStatus(Long userId, InvoiceStatus status);

    Optional<Invoice> findByInvoiceNumberAndUserId(String invoiceNumber, Long userId);
}
