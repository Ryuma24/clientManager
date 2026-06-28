package com.project.client.manager.repository;

import com.project.client.manager.model.Payment;
import com.project.client.manager.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment , Long> {

    List<Payment> findByInvoiceId(Long invoiceId);

    Optional<Payment> findByIdAndInvoiceId(Long id, Long invoiceId);

    List<Payment> findByStatus(PaymentStatus status);
}
